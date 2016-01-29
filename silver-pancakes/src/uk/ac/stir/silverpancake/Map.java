package uk.ac.stir.silverpancake;

import java.util.LinkedList;
import java.util.List;

public class Map {
	
	public Cell[][] values = {
		{new WallCell()  ,  new WallCell()  , new WallCell()},
		{new WaterCell() ,  new WaterCell() , new WallCell()},
		{new WaterCell() ,  new WallCell()  , new WallCell()}
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
	
}
