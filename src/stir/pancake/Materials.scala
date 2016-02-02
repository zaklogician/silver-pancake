package stir.pancake

import squants._
import squants.thermal.ThermalCapacity
import squants.thermal.JoulesPerKelvin
import squants.mass.KilogramsPerCubicMeter


sealed trait Material {
  val capacity: ThermalCapacity
  val density: Density
  // and a surprisingly good approximation
  lazy val conductionCoefficient: Double = density/KilogramsPerCubicMeter(2000)
}
case object Water   extends Material {
  val capacity: ThermalCapacity = JoulesPerKelvin(4182.0)
  val density: Density = KilogramsPerCubicMeter(986)
  def adjustedDensity(t: Temperature): Density = LinearDensity( Kelvin(303.15) -> KilogramsPerCubicMeter(995.7)
                                                              , Kelvin(343.15) -> KilogramsPerCubicMeter(977.8)
                                                              )(t)
}
case object Steel   extends Material {
  val capacity: ThermalCapacity = JoulesPerKelvin(2450.0) // normally 490, but less steel is used than acrylic
  val density: Density = KilogramsPerCubicMeter(7850)
}
case object Acrylic extends Material {
  val capacity: ThermalCapacity = JoulesPerKelvin(1470.0)
  val density: Density = KilogramsPerCubicMeter(1180)
}
case object Air     extends Material {
  val capacity: ThermalCapacity = JoulesPerKelvin(35.18)
  val density: Density = KilogramsPerCubicMeter(1.3)
}
case object HumanInWater extends Material {
  val capacity: ThermalCapacity = JoulesPerKelvin(3470.0)
  val density: Density = KilogramsPerCubicMeter(1000)
}