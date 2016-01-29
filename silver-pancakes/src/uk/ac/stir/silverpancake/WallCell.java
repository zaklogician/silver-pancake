package uk.ac.stir.silverpancake;

import java.awt.Color;

public class WallCell implements Cell {
	
	private int temperature = 30;
	@Override public double getTemperature() {
		return temperature;
	}
	@Override public void setTemperature(double temperature) {
		// fail silently (has constant temperature)
	}

	@Override public Color getColor() {
		return Color.BLACK;
	}
	@Override
	public Color temperatureColor() {
        return Color.black;
	}

}
