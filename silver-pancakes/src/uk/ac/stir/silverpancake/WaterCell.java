package uk.ac.stir.silverpancake;

import java.awt.Color;

public class WaterCell implements Cell {
	
	private int temperature = 50;
	@Override public int getTemperature() {
		return temperature;
	}
	@Override public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	@Override public Color getColor() {
		return Color.BLUE;
	}
	@Override
	public Color temperatureColor() {
		int color = 6*getTemperature() - 180;
		if( getTemperature() < 30 ) { color = 0;   }
		if( getTemperature() > 70 ) { color = 255; } 
		return new Color(color,0,0);
	}

}
