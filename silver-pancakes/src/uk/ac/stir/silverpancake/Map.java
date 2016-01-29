package uk.ac.stir.silverpancake;

import java.util.LinkedList;
import java.util.List;

public class Map {
	
	public Cell[][] values = {
		{new WallCell()  ,  new WallCell()  , new WallCell()},
		{new WaterCell() ,  new WaterCell() , new WallCell()},
		{new WaterCell() ,  new WallCell()  , new WallCell()}
	};
	
	public List<Cell> neighbors(int i, int j) {
		LinkedList<Cell> result = new LinkedList<Cell>();
		
		if (i != 0)               { result.add( values[i-1][j] ); }
		if (j != 0)               { result.add( values[i][j-1] ); }
		if (i != values.length-1) { result.add( values[i+1][j] ); }
		if (j != values.length-1) { result.add( values[i][j+1] ); }
		
		return result;
	}
	
}
