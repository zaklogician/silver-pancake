package uk.ac.stir.silverpancake;

public class Map {
	
	public static Cell[][] values = {
		{new WallCell() ,  new WallCell() , new WallCell()},
		{new WaterCell() ,  new WaterCell() , new WallCell()},
		{new WaterCell() ,  new WallCell() , new WallCell()}
	};
	
}
