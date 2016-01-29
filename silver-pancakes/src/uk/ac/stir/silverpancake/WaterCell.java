package uk.ac.stir.silverpancake;

import java.awt.Color;

public class WaterCell implements Cell {
	
	private int temperature = 30;
	@Override public int getTemperature() {
		return temperature;
	}
	@Override public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	@Override public Color getColor() {
		return Color.BLUE;
	}

}
