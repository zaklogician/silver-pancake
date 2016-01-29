package uk.ac.stir.silverpancake.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import uk.ac.stir.silverpancake.Cell;
import uk.ac.stir.silverpancake.Map;
import uk.ac.stir.silverpancake.Util;
import uk.ac.stir.silverpancake.WallCell;
import uk.ac.stir.silverpancake.WaterCell;

public class MapTest {
	
	class TestMap extends Map {
		public Cell[][] values = {
				{ new WaterCell(35), new WaterCell(35), new WaterCell(40) },
				{ new WaterCell(35), new WaterCell(40), new WaterCell(40) },
				{ new WaterCell(40), new WaterCell(40), new WaterCell(45) }
		};
	}
	
	class FinalMap extends Map {
		public Cell[][] values = {
				{ new WaterCell(33), new WaterCell(36), new WaterCell(35) },
				{ new WaterCell(36), new WaterCell(38), new WaterCell(39) },
				{ new WaterCell(35), new WaterCell(39), new WaterCell(37) }
		};
	}
	
	class UpdatedMap extends Map {
		
		public Cell[][] values;
		
		public UpdatedMap(Map old) {

			
		}
		
		
	}
	
	
	@Test
	public void testUpdate() {
		Map map = new TestMap();
		Cell[][] newValues = new Cell[map.values.length][map.values.length];
		
		for(int i = 0; i < map.values.length; i++)
	    for(int j = 0; j < map.values.length; j++) {
	    	newValues[i][j] = new WaterCell(Util.update(map.neighbors(i, j))); 
	    }
		
		Map expected = new FinalMap();
		
		for(int i = 0; i < expected.values.length; i++)
		for(int j = 0; j < expected.values.length; j++) {
		    	assertEquals("Coordinates "+i+","+j+" are not equal",expected.values[i][j].getTemperature(),newValues[i][j].getTemperature(),0.01);
		}
		
	}
	
	@Test
	public void testNeighbors() {
		
		Map map = new TestMap();
		Cell[][] n00 = map.neighbors(0, 0);
		assertEquals(  "Around 0,0 current should be 0,0", n00[1][1], map.values[0][0]);
		assertEquals(  "Around 0,0 right neighbor should be 0,1", n00[1][2], map.values[0][1]);
		assertEquals(  "Around 0,0 below neighbor should be 1,0", n00[2][1], map.values[1][0]);
	}

}
