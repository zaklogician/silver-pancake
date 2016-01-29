package uk.ac.stir.silverpancake;

import java.awt.Color;

public class WaterCell implements Cell {
	
	private double temperature;
	@Override public double getTemperature() {
		return temperature;
	}
	@Override public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	@Override public Color getColor() {
		return Color.BLUE;
	}
	@Override
	public Color temperatureColor() {
		int color = 6*(int)getTemperature() - 180;
		if( getTemperature() < 30 ) { color = 0;   }
		if( getTemperature() > 70 ) { color = 255; } 
		return new Color(color,0,0);
	}
	
	public WaterCell() { this.temperature = 30; }
	
	public WaterCell(double temperature) {
		this.temperature = temperature;
	}

}
