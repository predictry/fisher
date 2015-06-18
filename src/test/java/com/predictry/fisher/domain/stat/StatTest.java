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
		stat.addSales(1000.5);
		assertEquals(1000.5, stat.getSales(), 0.5);
		stat.addSales(50.5);
		assertEquals(1051, stat.getSales(), 0.5);
	}
	
	@Test
	public void addView() {
		Stat stat = new Stat();
		stat.addViews(10l);
		assertEquals(10l, stat.getViews().longValue());
		stat.addViews(15l);
		assertEquals(25l, stat.getViews().longValue());
	}
	
	@Test
	public void addItemPerCart() {
		Stat stat = new Stat();
		stat.addItemPerCart(10l);
		assertEquals(10l, stat.getItemPerCart().longValue());
		stat.addItemPerCart(10l);
		assertEquals(10l, stat.getItemPerCart().longValue());
		stat.addItemPerCart(20l);
		assertEquals(15l, stat.getItemPerCart().longValue());
		stat.addItemPerCart(5l);
		assertEquals(10l, stat.getItemPerCart().longValue());
	}
	
	@Test
	public void addItemPurchased() {
		Stat stat = new Stat();
		stat.addItemPurchased(10l);
		assertEquals(10l, stat.getItemPurchased().longValue());
		stat.addItemPurchased(20l);
		assertEquals(30l, stat.getItemPurchased().longValue());
	}
	
	@Test
	public void addUniqueVisitor() {
		Stat stat = new Stat();
		stat.addUniqueVisitor(100l);
		assertEquals(100l, stat.getUniqueVisitor().longValue());
		stat.addUniqueVisitor(150l);
		assertEquals(250l, stat.getUniqueVisitor().longValue());
	}
	
}
