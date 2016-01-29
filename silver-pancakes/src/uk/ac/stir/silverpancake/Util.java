package uk.ac.stir.silverpancake;

public class Util 
{
	public static double update(Cell[][] neighborhood) {
		
		double a = 0.2;
		double temperature = neighborhood[2][2].getTemperature() + a * (neighborhood[1][2].getTemperature() - 
							2 * neighborhood[2][2].getTemperature() + neighborhood[3][2].getTemperature()) + 
							a * (neighborhood[2][3].getTemperature() - 2 * neighborhood[2][2].getTemperature() +
							neighborhood[2][1].getTemperature() );

		return temperature;
	}
}
