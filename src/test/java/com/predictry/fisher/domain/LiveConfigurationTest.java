package com.predictry.fisher.domain;

import java.util.List;

import com.predictry.fisher.config.LiveConfiguration;

import static org.junit.Assert.*;

public class LiveConfigurationTest {

	public void testBlacklistTenants() {
		LiveConfiguration config = new LiveConfiguration();
		config.setBlacklistTenants("tenant1,tenant2,tenant3");
		List<String> results = config.getBlacklistTenants();
		assertEquals(3, results.size());
		assertEquals("tenant1", results.get(0));
		assertEquals("tenant2", results.get(1));
		assertEquals("tenant3", results.get(2));
	}
	
}
