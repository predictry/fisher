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
	
}
