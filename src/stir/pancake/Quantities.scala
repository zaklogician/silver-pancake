package stir.pancake

import squants.thermal._
import squants.energy._
import squants.mass.Density
import squants.mass.KilogramsPerCubicMeter
import squants.motion.Force
import squants.motion.Newtons

/**
 * Heat is determined by the number of calor particles
 */
object Calors {
  def apply(value: Int):    Energy = Joules(4182.0*value)
  def apply(value: Double): Energy = Joules(4182.0*value)
  def undo(from: Energy): Double = from.toJoules/4182
}

object CalorsPerKelvin {
  def apply(value: Double): ThermalCapacity = JoulesPerKelvin(4182.0*value)
}

object LinearDensity {
  
  def apply(x: (Temperature,Density), y: (Temperature,Density)): Temperature => Density = (i: Temperature) => {
      val a: Double = (y._2 - x._2).value / (y._1 - x._1).to(i.unit)
      val b: Double = x._2.value - a*(x._1.to(i.unit))
      KilogramsPerCubicMeter(a*i.value + b)
  }
  
}