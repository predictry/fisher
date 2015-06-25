package com.predictry.fisher.domain.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TopScoreTest {
	
	@Test
	public void addNewScore() {
		TopScore topScore = new TopScore();
		assertTrue(topScore.getItems().isEmpty());
		
		topScore.addNewScore("itemA", "Product A", "http://www.xxx.com", 10.0);
		assertEquals(1, topScore.getItems().size());
		assertEquals("itemA", topScore.getItemAtRank(1).getId());
		assertEquals("Product A", topScore.getItemAtRank(1).getName());
		assertEquals("http://www.xxx.com", topScore.getItemAtRank(1).getUrl());
		assertEquals(10.0, topScore.getItemAtRank(1).getScore(), 0.5);
		assertNull(topScore.getItemAtRank(2));
		
		topScore.addNewScore("itemB", "Product B", "http://www.xxx.com", 8.0);
		assertEquals(2, topScore.getItems().size());
		assertEquals("itemA", topScore.getItemAtRank(1).getId());
		assertEquals(10.0, topScore.getItemAtRank(1).getScore(), 0.5);
		assertEquals("itemB", topScore.getItemAtRank(2).getId());
		assertEquals(8.0, topScore.getItemAtRank(2).getScore(), 0.5);
		assertNull(topScore.getItemAtRank(3));
		
		topScore.addNewScore("itemC", "Product C", "http://www.yyy.com", 5.0);
		assertEquals(3, topScore.getItems().size());
		assertEquals("itemA", topScore.getItemAtRank(1).getId());
		assertEquals(10.0, topScore.getItemAtRank(1).getScore(), 0.5);
		assertEquals("itemB", topScore.getItemAtRank(2).getId());
		assertEquals(8.0, topScore.getItemAtRank(2).getScore(), 0.5);
		assertEquals("itemC", topScore.getItemAtRank(3).getId());
		assertEquals(5.0, topScore.getItemAtRank(3).getScore(), 0.5);
		assertNull(topScore.getItemAtRank(4));
		
		topScore.addNewScore("itemD", "Product D", "http://www.xxx.com", 20.0);
		assertEquals(4, topScore.getItems().size());
		assertEquals("itemD", topScore.getItemAtRank(1).getId());
		assertEquals(20.0, topScore.getItemAtRank(1).getScore(), 0.5);
		assertEquals("itemA", topScore.getItemAtRank(2).getId());
		assertEquals(10.0, topScore.getItemAtRank(2).getScore(), 0.5);
		assertEquals("itemB", topScore.getItemAtRank(3).getId());
		assertEquals(8.0, topScore.getItemAtRank(3).getScore(), 0.5);
		assertEquals("itemC", topScore.getItemAtRank(4).getId());
		assertEquals(5.0, topScore.getItemAtRank(4).getScore(), 0.5);
		
		topScore.addNewScore("itemE", "Product E", "http://www.xxx.com", 4.0);
		topScore.addNewScore("itemF", "Product F", "http://www.xxx.com", 3.0);
		topScore.addNewScore("itemG", "Product G", "http://www.xxx.com", 2.0);
		topScore.addNewScore("itemH", "Product H", "http://www.xxx.com", 1.5);
		topScore.addNewScore("itemI", "Product I", "http://www.xxx.com", 1.0);
		topScore.addNewScore("itemJ", "Product J", "http://www.xxx.com", 1.0);
		topScore.addNewScore("itemK", "Product K", "http://www.xxx.com", 1.0);
		topScore.addNewScore("itemL", "Product L", "http://www.xxx.com", 0.5);
		
		assertEquals(10, topScore.getItems().size());
		assertEquals("itemD", topScore.getItemAtRank(1).getId());
		assertEquals(20.0, topScore.getItemAtRank(1).getScore(), 0.5);
		assertEquals("itemA", topScore.getItemAtRank(2).getId());
		assertEquals(10.0, topScore.getItemAtRank(2).getScore(), 0.5);
		assertEquals("itemB", topScore.getItemAtRank(3).getId());
		assertEquals(8.0, topScore.getItemAtRank(3).getScore(), 0.5);
		assertEquals("itemC", topScore.getItemAtRank(4).getId());
		assertEquals(5.0, topScore.getItemAtRank(4).getScore(), 0.5);
		assertEquals("itemE", topScore.getItemAtRank(5).getId());
		assertEquals(4.0, topScore.getItemAtRank(5).getScore(), 0.5);
		assertEquals("itemF", topScore.getItemAtRank(6).getId());
		assertEquals(3.0, topScore.getItemAtRank(6).getScore(), 0.5);
		assertEquals("itemG", topScore.getItemAtRank(7).getId());
		assertEquals(2.0, topScore.getItemAtRank(7).getScore(), 0.5);
		assertEquals("itemH", topScore.getItemAtRank(8).getId());
		assertEquals(1.5, topScore.getItemAtRank(8).getScore(), 0.5);
		assertEquals("itemI", topScore.getItemAtRank(9).getId());
		assertEquals(1.0, topScore.getItemAtRank(9).getScore(), 0.5);
		assertEquals("itemJ", topScore.getItemAtRank(10).getId());
		assertEquals(1.0, topScore.getItemAtRank(10).getScore(), 0.5);
	}
	
	@Test(expected=RuntimeException.class)
	public void addDuplicatedIdInTopScore() {
		TopScore topScore = new TopScore();
		topScore.addNewScore("itemB", "Item B", "http://www.xxx.com", 5.0);
		topScore.addNewScore("itemB", "Item B", "http://www.yyy.com", 3.0);
	}

}
