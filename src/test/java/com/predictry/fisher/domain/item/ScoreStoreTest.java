package com.predictry.fisher.domain.item;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class ScoreStoreTest {

	@Test
	public void addNew() {
		ScoreStore store = new ScoreStore();
		ItemScore item1 = new ItemScore("item1", "Item 1", "http://xxx.yyy.com", 10.0);
		ItemScore item2 = new ItemScore("item2", "Item 2", "http://xxx.zzz.com", 5.0);
		ItemScore item3 = new ItemScore("item1", "Item 1", "http://xxx.yyy.com", 3.0);
		ItemScore item4 = new ItemScore("item2", "Item 2", "http://xxx.zzz.com", 2.0);
		store.add("tenant1", item1);
		store.add("tenant1", item2);
		store.add("tenant2", item3);
		store.add("tenant2", item4);

		List<ItemScore> itemScoreTenant1 = store.getStore("tenant1");
		assertTrue(itemScoreTenant1.contains(item1));
		assertTrue(itemScoreTenant1.contains(item2));
		int idxItem1 = itemScoreTenant1.indexOf(item1);
		int idxItem2 = itemScoreTenant1.indexOf(item2);
		assertEquals(10.0, itemScoreTenant1.get(idxItem1).getScore(), 0.1);
		assertEquals(5.0, itemScoreTenant1.get(idxItem2).getScore(), 0.1);
		
		List<ItemScore> itemScoreTenant2 = store.getStore("tenant2");
		assertTrue(itemScoreTenant2.contains(item3));
		assertTrue(itemScoreTenant2.contains(item4));
		int idxItem3 = itemScoreTenant2.indexOf(item3);
		int idxItem4 = itemScoreTenant2.indexOf(item4);
		assertEquals(3.0, itemScoreTenant2.get(idxItem3).getScore(), 0.1);
		assertEquals(2.0, itemScoreTenant2.get(idxItem4).getScore(), 0.1);
	}
	
	@Test
	public void addExisting() {
		ScoreStore store = new ScoreStore();
		ItemScore item1 = new ItemScore("item1", "Item 1", "http://xxx.yyy.com", 10.0);
		ItemScore item2 = new ItemScore("item1", "Item 1", "http://xxx.yyy.com", 5.0);
		ItemScore item3 = new ItemScore("item1", "Item 1", "http://xxx.yyy.com", 3.0);
		ItemScore item4 = new ItemScore("item1", "Item 1", "http://xxx.yyy.com", 2.0);
		store.add("tenant1", item1);
		store.add("tenant1", item2);
		store.add("tenant2", item3);
		store.add("tenant2", item4);

		List<ItemScore> itemScoreTenant1 = store.getStore("tenant1");
		assertEquals("item1", itemScoreTenant1.get(0).getId());
		assertEquals("Item 1", itemScoreTenant1.get(0).getName());
		assertEquals(15.0, itemScoreTenant1.get(0).getScore(), 0.5);
		
		List<ItemScore> itemScoreTenant2 = store.getStore("tenant2");
		assertEquals("item1", itemScoreTenant2.get(0).getId());
		assertEquals("Item 1", itemScoreTenant2.get(0).getName());
		assertEquals(5.0, itemScoreTenant2.get(0).getScore(), 5.0);
	}
	
}
