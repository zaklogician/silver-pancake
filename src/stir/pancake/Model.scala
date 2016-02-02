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
  
  // TODO: slightly hackish way of doing it
  //       because Scala RNG has no way method
  //       to partition an integer
  def diffuse1(c: IndexC): Unit = for (n <- 1 to backbuffer(c.address)) c.neighbor(RNG.nextDirection) match {
    case Just(n) => if( RNG.nextDouble < n.material.capacity/c.material.capacity) {
      backbuffer.update(n.address, backbuffer(n.address) + 1)
      backbuffer.update(c.address, backbuffer(c.address) - 1)
    }
    case Empty() => Unit
  }
  
  def conduct1(c: IndexC): Unit = for (n <- 1 to backbuffer(c.address)) c.neighbor(RNG.nextDirection) match {
      case Just(n) => if( n.material == c.material && RNG.nextDouble > c.material.conductionCoefficient ) {
        backbuffer.update(n.address, backbuffer(n.address) + 1)
        backbuffer.update(c.address, backbuffer(c.address) - 1)
      }
      case Empty() => Unit
  }
  
  def thermoregulate(c: IndexC): Unit = if (c.temperature < Kelvin(308)) {
    if( RNG.nextDouble < c.material.conductionCoefficient/2/CperM/CperM) backbuffer.update(c.address, backbuffer(c.address) + 1)
  } else if (c.temperature > Kelvin(311)) {
    if( RNG.nextDouble < c.material.conductionCoefficient/2/CperM/CperM) backbuffer.update(c.address, backbuffer(c.address) - 1)
  }
  
  def scale(c: IndexC, scalar: Double): Unit = {
    val current = backbuffer(c.address)
    backbuffer.update(c.address, (current*scalar).round.toInt)
  }
  
  def heat(m: IndexM, tapT: Temperature, tapV: Volume): Unit = m.support.foreach { c =>
    val toAddPerCell: Int = Calors.undo(Joules(Water.capacity.toJoulesPerKelvin * tapT.toKelvinScale * (tapV.toCubicMeters/0.09))).round.toInt / (CperM * CperM)
    val toScale: Double = 1 - (tapV.toCubicMeters/0.09)
    m.support.foreach { c =>
      backbuffer.update(c.address, (backbuffer(c.address) * toScale).round.toInt + toAddPerCell)
    }
  }
  
  def mix(m: IndexM, dir: Direction): Unit = m.neighbor(dir) match {
    case Just(n) => {
      val cells = m.support ++ n.support
      var currentCalors = n.calors + m.calors
      cells foreach { c => backbuffer.update(c.address, 0) }
      while (currentCalors > 0) {
        val cell = cells(RNG.nextInt(cells.length))
        var calorsToAdd = RNG.nextInt(currentCalors/10 + 1)
        if (calorsToAdd < 1) calorsToAdd = 1
        backbuffer.update(cell.address, backbuffer(cell.address) + calorsToAdd)
        currentCalors = currentCalors - calorsToAdd
      }
    }
    case Empty() => Unit
  }
  
  def mixStroke(m: IndexM, dir: Direction, strength: Int): Unit = {
    var currentCell = m
    var currentStrength = strength
    while (currentStrength > 0) {
      mix(currentCell, dir)
      currentStrength = currentStrength - 1
      currentCell = m.unsafeNeighbor(dir)
      if (!m.valid) return
    }
  }
  
  
  def topAirLoss(c: IndexC): Unit = for (n <- 1 to backbuffer(c.address)) if( c.material == Water && RNG.nextDouble*(if (bubbles) CperM*CperM else 1) < Air.capacity/Water.capacity) {
    backbuffer.update(c.address, backbuffer(c.address) - 1)
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