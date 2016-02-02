package stir.pancake.program

import stir.pancake._

import squants._
import squants.space.CubicMeters
import squants.energy.Joules
import squants.energy.EnergyDensity
import squants.energy.JoulesPerCubicMeter
import squants.mass.KilogramsPerCubicMeter
import squants.thermal.ThermalCapacity
import squants.thermal.JoulesPerKelvin
import squants.motion.Force
import squants.motion.Newtons
import squants.thermal.Celsius

object Model1 extends Model {
  val CperM = 3
  val columnsM = 32
  val rowsM = 17
  
  
  //// CONFIGURATION SECTION ////
  
  // is this a bubble bath?
  val bubbles: Boolean = false
  
  // how much energy per tile initially?
  val initialCalors: Int = 15
  
  
  // properties of the hot water faucet:
  val tapLocation: IndexM = IndexC(12,25).toIndexM
  val tapTemperature = Celsius(50)
  val tapCoeff: Double = 0.09                            // 0.09 corresponds to 1/6 liters per second
  
  // properties of the hand movement 1
  val force1On: Boolean = false
  val force1Location: IndexM = IndexC(62,12).toIndexM    // 31,12 right; 62,12 left
  val force1Direction: Direction = Left                    // Up, Down, Left, Right
  val force1Stroke: Int = 4
  
  // properties of the hand movement 2
  val force2On: Boolean = false
  val force2Location: IndexM = IndexC(31, 40).toIndexM // 31,40 right; 62,40 left
  val force2Direction: Direction = Right
  val force2Stroke: Int = 4
  
  val wallMaterial: Material = Acrylic           // Steel or Acrylic
  
  val forceFreq: Int = 1
  
  
  override def runIteration: Unit = transaction {
    materials.foreach { m => 
      diffuseByTemperature(m)
    }
    cells.foreach { c => 
      if (c.material == Air) evacuate(c) 
      if (c.material == Water || c.material == HumanInWater) topAirLoss(c)
    }
    //tap
    if (force1On && currentIteration % forceFreq == 0) mixByStroke(force1Location,force1Direction,force1Stroke)
    if (force2On && currentIteration % forceFreq == 0) mixByStroke(force2Location,force2Direction,force2Stroke)
    currentIteration = currentIteration + 1
  }
  
  def tap: Unit = {
    // heating effect
    heat(tapLocation,tapTemperature,tapCoeff)
    // splashing effect
    if (RNG.nextDouble < 0.2) mixByStroke(tapLocation,Right,4)
    else if (RNG.nextDouble < 0.2) mixByStroke(tapLocation,RNG.nextDirection,1)
  }
  
  //// INITIALIZATION ////
  // draw bathtub
  var gridM: Array[Material] = Array.ofDim(columnsM*rowsM)
  var gridC: Array[Int] = Array.ofDim(columnsC*rowsC)
  
  materials foreach { m => 
    if (m.column == 0 || m.row == 0 || m.column == columnsM-1 || m.row == rowsM-1) gridM.update(m.address, Air)
    else if (m.column == 1 || m.row == 1 || m.column == columnsM-2 || m.row == rowsM-2) gridM.update(m.address, wallMaterial)
    else gridM.update(m.address, Water)
  }
  
  // draw human
  materials foreach { m => 
    if (m.row > 5 && m.row < 12 && m.column > 9 && m.column < 27) gridM.update(m.address, HumanInWater)
    if (m.column == 26 && (m.row == 6 || m.row == 11)) gridM.update(m.address, Water)
  }
  
  cells foreach { c => if (c.material == Water) {
    // each cell should have ~10 calors in it
    val rand = RNG.nextDouble
    val n = if (rand < 0.34) -1 else if (rand < 0.66) 0 else 1
    gridC.update(c.address, initialCalors+n)//10 + n)
  }}

  cells foreach { c => if (c.material == wallMaterial) {
    // each cell should have ~10 calors in it
    val rand = RNG.nextDouble
    val n = if (rand < 0.34) -1 else if (rand < 0.66) 0 else 1
    val factor = wallMaterial match {
      case Steel   => 13
      case Acrylic => 8
      case _       =>  0
    }
    gridC.update(c.address, Math.max(0,initialCalors+n-factor))
  }}
  
  cells foreach { c => if (c.material == HumanInWater) {
    gridC.update(c.address, initialCalors - 2)//9)
  }}

}