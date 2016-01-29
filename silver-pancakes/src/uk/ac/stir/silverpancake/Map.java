package uk.ac.stir.silverpancake;

import java.util.LinkedList;
import java.util.List;

public class Map {
	
	public Cell[][] values = {
			{ new WaterCell(35), new WaterCell(35), new WaterCell(40) },
			{ new WaterCell(35), new WaterCell(40), new WaterCell(40) },
			{ new WaterCell(40), new WaterCell(40), new WaterCell(45) }
	};
	
	public final Cell border = new WallCell();
	
	public Cell[][] neighbors(int i, int j) {
		Cell[][] result = new Cell[3][3];
		
		for( int ic = 0; ic < 3; ic++ )
		for( int jc = 0; jc < 3; jc++ ) {
			result[ic][jc] = border;
		}
		result[1][1] = values[i][j];
		if (i != 0) result[0][1] = values[i-1][j];
		if (j != 0) result[1][0] = values[i][j-1];
		if (i != values.length-1) result[2][1] = values[i+1][j];
		if (j != values.length-1) result[1][2] = values[i][j+1];
		
		return result;
	}
	
    public static double temperatureFormula(Cell[][] neighborhood) {
		double a = 0.2;
		double temperature = neighborhood[1][1].getTemperature() + a * (neighborhood[0][1].getTemperature() - 
							2 * neighborhood[1][1].getTemperature() + neighborhood[2][1].getTemperature()) + 
							a * (neighborhood[1][2].getTemperature() - 2 * neighborhood[1][1].getTemperature() +
							neighborhood[1][0].getTemperature() );

		return temperature;
	}
	
	public void iteration() {
		Cell[][] newValues = new Cell[values.length][values.length];
		
		for(int i = 0; i < values.length; i++)
		for(int j = 0; j < values.length; j++) {
			System.out.println("Original temperature: " + values[i][j].getTemperature());
			double temp = temperatureFormula(neighbors(i, j));
		    newValues[i][j] = new WaterCell(temp); 
		    System.out.println("Temperature set to: " + temp);
		    System.out.println("Temperature set to: " + newValues[i][j].getTemperature());
		}
		System.out.println("Iteration passed " + this.values[0][0].getTemperature());
		this.values = newValues;
		System.out.println("Iteration passed " + this.values[0][0].getTemperature());
	}
	
}
