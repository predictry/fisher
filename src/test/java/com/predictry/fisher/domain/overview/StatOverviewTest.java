package com.predictry.fisher.domain.overview;

import org.junit.Test;
import static org.junit.Assert.*;

public class StatOverviewTest {

	@Test
	public void conversionRate() {
		StatOverview statOverview = new StatOverview();
		statOverview.setPageView(new Value<Long>(0l, 0l, 0l));
		statOverview.setOrders(0l);
		assertEquals(0.0, statOverview.getConversionRate(), 0.01);
		
		statOverview.setPageView(new Value<Long>(1000l, 0l, 0l));
		statOverview.setOrders(100l);
		assertEquals(0.1, statOverview.getConversionRate(), 0.01);
		
		statOverview.setPageView(new Value<Long>(1000l, 0l, 0l));
		statOverview.setOrders(800l);
		assertEquals(0.8, statOverview.getConversionRate(), 0.01);
	}
	
	@Test
	public void itemPerCart() {
		StatOverview statOverview = new StatOverview();
		statOverview.setOrders(0l);
		statOverview.setItemPurchased(new Value<Long>(0l, 0l, 0l));
		assertEquals(0l, statOverview.getItemPerCart().getOverall().longValue());
		
		statOverview.setOrders(10l);
		statOverview.setItemPurchased(new Value<Long>(1000l, 0l, 0l));
		assertEquals(100l, statOverview.getItemPerCart().getOverall().longValue());
		
		statOverview.setOrders(50l);
		statOverview.setItemPurchased(new Value<Long>(120l, 0l, 0l));
		assertEquals(2l, statOverview.getItemPerCart().getOverall().longValue());
	}
	
}
