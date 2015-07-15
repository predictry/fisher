package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;
import static org.junit.Assert.*;
import org.junit.Test;

public class StatTest {
	
	@Test
	public void getIndexName() {
		Stat stat = new Stat();
		stat.setTimeFrom(LocalDateTime.parse("2015-12-01T10:00:00"));
		assertEquals("stat_2015", stat.getIndexName());
		stat.setTimeFrom(LocalDateTime.parse("2013-12-01T10:00:00"));
		assertEquals("stat_2013", stat.getIndexName());
	}

	@Test
	public void isForTime() {
		Stat stat = new Stat();
		stat.setTimeFrom(LocalDateTime.parse("2015-12-01T10:00:00"));
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
	
}
