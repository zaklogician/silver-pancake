package uk.ac.stir.silverpancake.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import uk.ac.stir.silverpancake.Cell;
import uk.ac.stir.silverpancake.Map;
import uk.ac.stir.silverpancake.WallCell;
import uk.ac.stir.silverpancake.WaterCell;

public class MapTest {
	
	class TestMap extends Map {
		public Cell[][] values = {
				{new WallCell()  ,  new WallCell()  , new WallCell()},
				{new WaterCell() ,  new WaterCell() , new WallCell()},
				{new WaterCell() ,  new WallCell()  , new WallCell()}
		};
	}
	
	
	@Test
	public void testNeighbors() {
		Map map = new TestMap();
		
		List<Cell> n00 = map.neighbors(0, 0);
		assertTrue(  "0,1 is not a neighbor of 0,0 but it should be", n00.contains(map.values[0][1]) );
		assertTrue(  "1,0 is not a neighbor of 0,0 but it should be", n00.contains(map.values[1][0]) );
		assertFalse( "1,1 is a neighbor of 0,0 but it should not be", n00.contains(map.values[1][1]) );
		
		List<Cell> n01 = map.neighbors(0, 1);
		assertTrue(  "0,0 is not a neighbor of 0,1 but it should be", n01.contains(map.values[0][0]) );
		assertTrue(  "1,1 is not a neighbor of 0,1 but it should be", n01.contains(map.values[1][1]) );
		assertTrue(  "0,2 is not a neighbor of 0,1 but it should be", n01.contains(map.values[0][2]) );
		assertFalse( "1,2 is a neighbor of 0,1 but it should not be", n01.contains(map.values[1][2]) );
		
		List<Cell> n11 = map.neighbors(1, 1);
		assertTrue(  "0,1 is not a neighbor of 1,1 but it should be", n11.contains(map.values[0][1]) );
		assertTrue(  "1,0 is not a neighbor of 1,1 but it should be", n11.contains(map.values[1][0]) );
		assertTrue(  "1,2 is not a neighbor of 1,1 but it should be", n11.contains(map.values[1][2]) );
		assertTrue(  "2,1 is not a neighbor of 1,1 but it should be", n11.contains(map.values[2][1]) );
		assertFalse( "0,0 is a neighbor of 1,1 but it should not be", n11.contains(map.values[0][0]) );
		
		List<Cell> n22 = map.neighbors(2, 2);
		assertTrue(  "1,2 is not a neighbor of 1,1 but it should be", n22.contains(map.values[1][2]) );
		assertTrue(  "1,0 is not a neighbor of 1,1 but it should be", n11.contains(map.values[1][0]) );
		assertTrue(  "1,2 is not a neighbor of 1,1 but it should be", n11.contains(map.values[1][2]) );
		assertTrue(  "2,1 is not a neighbor of 1,1 but it should be", n11.contains(map.values[2][1]) );
		assertFalse( "0,0 is a neighbor of 1,1 but it should not be", n11.contains(map.values[0][0]) );
		
		
	}

}
