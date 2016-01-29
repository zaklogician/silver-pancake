package uk.ac.stir.silverpancake;

import java.awt.Color;

public interface Cell {
	
	public Color getColor();
	
	public double getTemperature();
	public void setTemperature(double temperature);
	
	public Color temperatureColor();
	
}
