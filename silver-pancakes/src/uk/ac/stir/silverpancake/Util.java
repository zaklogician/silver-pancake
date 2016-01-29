package uk.ac.stir.silverpancake;

public class Util 
{
	public static double update(Cell[][] neighborhood) {
		
		double a = 0.2;
		double temperature = neighborhood[1][1].getTemperature() + a * (neighborhood[0][1].getTemperature() - 
							2 * neighborhood[1][1].getTemperature() + neighborhood[2][1].getTemperature()) + 
							a * (neighborhood[1][2].getTemperature() - 2 * neighborhood[1][1].getTemperature() +
							neighborhood[1][0].getTemperature() );

		return temperature;
	}
}
