package uk.ac.stir.silverpancake;

import java.awt.Color;

public interface Cell {
	
	public Color getColor();
	
	public int getTemperature();
	public void setTemperature(int temperature);
	
	public Color temperatureColor();
	
}
