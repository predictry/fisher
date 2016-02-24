package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;
import static org.junit.Assert.*;
import org.junit.Test;

public class StatTest {
	
	@Test
	public void getIndexName() {
		Stat stat = new Stat();
		stat.setTime(LocalDateTime.parse("2015-12-01T10:00:00"));
		assertEquals("stat_2015", stat.getIndexName());
		stat.setTime(LocalDateTime.parse("2013-12-01T10:00:00"));
		assertEquals("stat_2013", stat.getIndexName());
	}

	@Test
	public void isForTime() {
		Stat stat = new Stat();
		stat.setTime(LocalDateTime.parse("2015-12-01T10:00:00"));
		assertTrue(stat.isForTime(LocalDateTime.parse("2015-12-01T10:10:10")));
		assertTrue(stat.isForTime(LocalDateTime.parse("2015-12-01T10:00:01")));
		assertTrue(stat.isForTime(LocalDateTime.parse("2015-12-01T10:59:59")));
		assertFalse(stat.isForTime(LocalDateTime.parse("2015-12-02T10:00:10")));
		assertFalse(stat.isForTime(LocalDateTime.parse("2015-12-02T20:00:00")));
	}
	
	@Test
	public void addSales() {
		Stat stat = new Stat();
		stat.addSales(new Value(1000.5, 100.0, 0.0));
		assertEquals(new Value(1000.5, 100.0, 0.0), stat.getSales());
		stat.addSales(new Value(50.5, 10.0, 0.0));
		assertEquals(new Value(1051.0, 110.0, 0.0), stat.getSales());
	}
	
	@Test
	public void addView() {
		Stat stat = new Stat();
		stat.addViews(new Value(10.0, 100.0, 0.0));
		assertEquals(new Value(10.0, 100.0, 0.0), stat.getViews());
		stat.addViews(new Value(15.0, 10.0, 0.0));
		assertEquals(new Value(25.0, 110.0, 0.0), stat.getViews());
	}
		
	@Test
	public void addItemPurchased() {
		Stat stat = new Stat();
		stat.addItemPurchased(new Value(10.0, 100.0, 0.0));
		assertEquals(new Value(10.0, 100.0, 0.0), stat.getItemPurchased());
		stat.addItemPurchased(new Value(20.0, 10.0, 0.0));
		assertEquals(new Value(30.0, 110.0, 0.0), stat.getItemPurchased());
	}
	
	@Test
	public void addUniqueVisitor() {
		Stat stat = new Stat();
		stat.addUniqueVisitor(new Value(100.0, 10.0, 0.0));
		assertEquals(new Value(100.0, 10.0, 0.0), stat.getUniqueVisitor());
		stat.addUniqueVisitor(new Value(150.0, 20.0, 0.0));
		assertEquals(new Value(250.0, 30.0, 0.0), stat.getUniqueVisitor());
	}
	
	@Test
	public void addUniqueItemPurchased() {
		Stat stat = new Stat();
		stat.addUniqueItemPurchased(new Value(100.0, 10.0, 0.0));
		assertEquals(new Value(100.0, 10.0, 0.0), stat.getUniqueItemPurchased());
		stat.addUniqueItemPurchased(new Value(150.0, 20.0, 0.0));
		assertEquals(new Value(250.0, 30.0, 0.0), stat.getUniqueItemPurchased());
	}
	
	@Test
	public void merge() {
		Stat stat1 = new Stat();
		Stat stat2 = new Stat(LocalDateTime.now().toString(), "tenant1", 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
		stat1.merge(stat2);
		assertEquals(10.0, stat1.getViews().getOverall(), 0.1);
		assertEquals(20.0, stat1.getSales().getOverall(), 0.1);
		assertEquals(30.0, stat1.getItemPurchased().getOverall(), 0.1);
		assertEquals(40.0, stat1.getOrders().getOverall(), 0.1);
		assertEquals(50.0, stat1.getUniqueVisitor().getOverall(), 0.1);
		assertEquals(60.0, stat1.getUniqueItemPurchased().getOverall(), 0.1);
		Stat stat3 = new Stat(LocalDateTime.now().toString(), "tenant1", 10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
		stat1.merge(stat3);
		assertEquals(20.0, stat1.getViews().getOverall(), 0.1);
		assertEquals(40.0, stat1.getSales().getOverall(), 0.1);
		assertEquals(60.0, stat1.getItemPurchased().getOverall(), 0.1);
		assertEquals(80.0, stat1.getOrders().getOverall(), 0.1);
		assertEquals(100.0, stat1.getUniqueVisitor().getOverall(), 0.1);
		assertEquals(120.0, stat1.getUniqueItemPurchased().getOverall(), 0.1);
	}
	
}
