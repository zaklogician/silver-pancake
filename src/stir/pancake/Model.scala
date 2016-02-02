package stir.pancake


import stir.pancake.Transactions._

import squants._
import squants.energy.Joules
import squants.energy.EnergyDensity
import squants.energy.JoulesPerCubicMeter
import squants.mass.KilogramsPerCubicMeter
import squants.thermal.ThermalCapacity
import squants.thermal.JoulesPerKelvin
import squants.motion.Force
import squants.motion.Newtons

import scalaz._
import scalaz.Maybe._

//// PHYSICAL QUANTITIES ////


trait Model {
  
  //// UNIVERSAL CONSTANTS ////
  val columnsM: Int
  val rowsM:    Int
  val CperM: Int
  val air: Temperature = Kelvin(298.15)
  val bubbles: Boolean
  
  //// GAME BOARD DATA ////
  var gridC: Array[Int]
  var gridM: Array[Material]
  var currentIteration: Int = 0
  
  //// ALGORITHM ////
  def runIteration: Unit
  
  
  //// DERIVED CONSTANTS ////
  lazy val columnsC: Int = columnsM*CperM
  lazy val rowsC: Int = rowsM*CperM
  lazy val backbuffer: Array[Int] = {
    val backbuffer: Array[Int] = Array.ofDim(gridC.length)
    for(x <- 0 until backbuffer.length) backbuffer.update(x, gridC(x))
    backbuffer
  }
  
  //// IMPLEMENTATION ////
  
  
  
  case class IndexC(column: Int, row: Int) {
    val address: Int = column + row*columnsC
    def valid: Boolean = (0 <= column && column < columnsC && 0 <= row && row < rowsC)
    private[Model] def unsafeNeighbor(dir: Direction): IndexC = IndexC(column+dir.column, row+dir.row)
    val toIndexM: IndexM = IndexM(column/CperM,row/CperM)
    
    def calors: Int = gridC(address)
    
    def neighbor(dir: Direction): Maybe[IndexC] = {
      val adjacent = unsafeNeighbor(dir)
      if (adjacent.valid) Just( adjacent ) else Empty()
    }
    
    def isEmpty: Boolean = calors == 0
    def temperature: Temperature = toIndexM.temperature
    def material: Material = toIndexM.material
  }
  
  case class IndexM(column: Int, row: Int) {
    val address: Int = column + row*columnsM
    def valid: Boolean = (0 <= column && column < columnsM && 0 <= row && row < rowsM)
    private[Model] def unsafeNeighbor(dir: Direction): IndexM = IndexM(column+dir.column, row+dir.row)
    def neighbor(dir: Direction): Maybe[IndexM] = {
      val adjacent = unsafeNeighbor(dir)
      if (adjacent.valid) Just( adjacent ) else Empty()
    }
    
    def support: IndexedSeq[IndexC] = for {
      c <- 0 until CperM
      r <- 0 until CperM
    } yield IndexC(CperM*column+c,CperM*row+r)
    
    def material: Material = gridM(address)
    def calors: Int = support.map(_.calors).sum
    def temperature: Temperature = air + Calors(calors/CperM/CperM)/material.capacity
  }
  
  def cells: IndexedSeq[IndexC] = for {
    c <- 0 until columnsC
    r <- 0 until rowsC
  } yield IndexC(c,r)
  
  def materials: IndexedSeq[IndexM] = for {
    c <- 0 until columnsM
    r <- 0 until rowsM
  } yield IndexM(c,r)
  
  def diffuse1(c: IndexC): Unit = {
    val buckets: Array[Int] = Array.ofDim(4)
    val distribution: List[Double] = Direction.list.map { dir =>
      c.neighbor(dir) match {
        case Just(n) => n.material.capacity/c.material.capacity
        case Empty() => 0
      }
    }
    for (n <- 1 to backbuffer(c.address)) {
      val pick = RNG.weightedPick(distribution)
      buckets.update(pick, buckets(pick)+1)
    }
    for { n <- c.neighbor(Up)    } yield backbuffer.update(n.address, backbuffer(n.address) + buckets(0))
    for { n <- c.neighbor(Down)  } yield backbuffer.update(n.address, backbuffer(n.address) + buckets(1)) 
    for { n <- c.neighbor(Left)  } yield backbuffer.update(n.address, backbuffer(n.address) + buckets(2)) 
    for { n <- c.neighbor(Right) } yield backbuffer.update(n.address, backbuffer(n.address) + buckets(3)) 
    backbuffer.update(c.address, 0)
  }
  
  // thermoregulation deactivated by default since it's not calibrated yet, and it takes a long time
  def thermoregulate(c: IndexC): Unit = if (c.temperature < Kelvin(308)) {
    if( RNG.nextDouble < c.material.conductionCoefficient/2/CperM/CperM) backbuffer.update(c.address, backbuffer(c.address) + 1)
  } else if (c.temperature > Kelvin(311)) {
    if( RNG.nextDouble < c.material.conductionCoefficient/2/CperM/CperM) backbuffer.update(c.address, backbuffer(c.address) - 1)
  }
  
  def scale(c: IndexC, scalar: Double): Unit = {
    val current = backbuffer(c.address)
    backbuffer.update(c.address, (current*scalar).round.toInt)
  }
  
  def heat(m: IndexM, target: Temperature, volumeCoeff: Double): Unit = if (target != m.temperature) {
    val targetCalors: Int = (Calors.undo(target * m.material.capacity)/CperM/CperM).round.toInt
    if (RNG.nextDouble > 1/Math.abs((target-m.temperature).toCelsiusScale)) m.support foreach { c =>
      if (RNG.nextDouble > volumeCoeff) backbuffer.update(c.address, targetCalors)
    }
  }
  
  def equalize(m: IndexM, dir: Direction): Unit = m.neighbor(dir) match {
    case Just(n) => (m.support,n.support).zipped foreach { (c,d) =>
      val combinedCalors = backbuffer(c.address) + backbuffer(d.address)
      val cNew = combinedCalors / 2
      val dNew = combinedCalors - cNew
      backbuffer.update(c.address, cNew)
      backbuffer.update(d.address, dNew)
    }
    case Empty() => Unit
  }
  
  def smoothMix(m: IndexM, dir: Direction): Unit = m.neighbor(dir) match {
    case Just(n) => {
      val mAvg = m.calors/CperM/CperM
      val nAvg = n.calors/CperM/CperM
      (m.support,n.support).zipped foreach { (c,d) => 
        val cCurrent = backbuffer(c.address)
        val dCurrent = backbuffer(d.address)
        if (Math.abs(dCurrent-mAvg) < Math.abs(cCurrent-nAvg)) {
          backbuffer.update(c.address, dCurrent)
          backbuffer.update(d.address, cCurrent)
        }
      }
    }
    case Empty() => Unit
  }
  
  def mix(m: IndexM, dir: Direction): Unit = m.neighbor(dir) match {
    case Just(n) => (m.support,n.support).zipped foreach { (c,d) =>
      val cHeat = backbuffer(c.address)
      val dHeat = backbuffer(d.address)
      if (RNG.nextDouble < 0.25) {
        backbuffer.update(c.address, dHeat)
        backbuffer.update(d.address, cHeat)
      }
    }
    case Empty() => Unit
  }
  
  def mixByStroke(m: IndexM, dir: Direction, strength: Int): Unit = {
    var currentCell = m
    var currentStrength = strength
    while (currentStrength > 0) {
      smoothMix(currentCell, dir)
      currentStrength = currentStrength - 1
      currentCell = m.unsafeNeighbor(dir)
      if (!m.valid) return
      if (m.material != Water && m.material != HumanInWater) return
    }
  }
  
  def diffuseByTemperature(m: IndexM): Unit = if (RNG.nextDouble > 1/m.temperature.toKelvinScale) m.support.foreach(diffuse1)
  
  def topAirLoss(c: IndexC): Unit = {
    val surfaceProb   = 0.14                     // the probability that calor is on surface, since water is 7 units high
    val freedomFactor = 6                        // a 3d calor has 6 degrees of freedom
    val bubblesFactor = if (bubbles) 6 else 1    // bubbles form an additional cell layer of air on top
    val diffusionProb = Air.capacity/c.material.capacity
    if (RNG.nextDouble < surfaceProb*diffusionProb) {
      val calors = backbuffer(c.address)
      if (calors > 0) backbuffer.update(c.address, calors - (calors/freedomFactor/bubblesFactor))
    }
  }
  
  def evacuate(c: IndexC): Unit = backbuffer.update(c.address, 0)
  
  def drainC(c: IndexC): Unit = backbuffer.update(c.address, 0)
  def drainM(m: IndexM): Unit = m.support foreach { c =>
    if (RNG.nextDouble < 1/CperM) drainC(c)
  }
  
  
  def transaction[A](f: => Unit): Unit = {
    for(x <- 0 until backbuffer.length) backbuffer.update(x, gridC(x))
    f
    for(x <- 0 until gridC.length) gridC.update(x, backbuffer(x))
  }
  
  def waterMaterial: IndexedSeq[IndexM] = materials.filter(m => m.material == Water || m.material == HumanInWater)
  def tempAverage: Temperature = waterMaterial.map(_.temperature).reduce(_+_)/waterMaterial.length
  def tempStdev: Double = Math.sqrt( waterMaterial.map { m =>
    val x = m.temperature.to(Kelvin)
    (x - tempAverage.to(Kelvin))*(x - tempAverage.to(Kelvin))/waterMaterial.length
  }.sum )
  
}