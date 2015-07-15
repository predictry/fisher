package com.predictry.fisher.domain.overview;

import org.junit.Test;

import com.predictry.fisher.domain.stat.StatOverview;
import com.predictry.fisher.domain.stat.Value;

import static org.junit.Assert.*;

public class StatOverviewTest {

	@Test
	public void conversionRate() {
		StatOverview statOverview = new StatOverview();
		statOverview.setPageView(new Value());
		statOverview.setOrders(new Value());
		assertEquals(new Value(), statOverview.getConversionRate());
		
		statOverview.setPageView(new Value(1000.0, 0.0, 0.0));
		statOverview.setOrders(new Value(100.0, 0.0, 0.0));
		assertEquals(new Value(0.1, 0.0, 0.0), statOverview.getConversionRate());
		
		statOverview.setPageView(new Value(1000.0, 0.0, 0.0));
		statOverview.setOrders(new Value(800.0, 0.0, 0.0));
		assertEquals(new Value(0.8, 0.0, 0.0), statOverview.getConversionRate());
	}
	
	@Test
	public void itemPerCart() {
		StatOverview statOverview = new StatOverview();
		statOverview.setOrders(new Value());
		statOverview.setItemPurchased(new Value());
		assertEquals(new Value(), statOverview.getItemPerCart());
		
		statOverview.setOrders(new Value(10.0, 0.0, 0.0));
		statOverview.setItemPurchased(new Value(1000.0, 0.0, 0.0));
		assertEquals(new Value(100.0, 0.0, 0.0), statOverview.getItemPerCart());
		
		statOverview.setOrders(new Value(60.0, 0.0, 0.0));
		statOverview.setItemPurchased(new Value(120.0, 0.0, 0.0));
		assertEquals(new Value(2.0, 0.0, 0.0), statOverview.getItemPerCart());
	}
	
}
