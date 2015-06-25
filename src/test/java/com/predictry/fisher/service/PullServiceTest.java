package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.stat.Stat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class PullServiceTest {

	@Autowired
	private PullService pullService;
	
	@Test
	public void aggregateFile() throws IOException {
		File file = new File(getClass().getResource("/sample.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		Map<String, Stat> stats = pullService.aggregate(sources, LocalDateTime.parse("2015-06-19T03:00:00"));
		
		// Stat for 'tenant1'
		Stat statTenant1 = stats.get("tenant1");
		assertEquals(2l, statTenant1.getViews().longValue());
		assertEquals(1000001.1, statTenant1.getSales(), 0.5);
		assertEquals(15l, statTenant1.getItemPerCart().longValue());
		assertEquals(28l, statTenant1.getItemPurchased().longValue());
		assertEquals(2l, statTenant1.getUniqueVisitor().longValue());
		assertEquals(4l, statTenant1.getOrders().longValue());
		
		// Stat for 'tenant2'
		Stat statTenant2 = stats.get("tenant2");
		assertEquals(3l, statTenant2.getViews().longValue());
		assertEquals(18000.0, statTenant2.getSales(), 0.5);
		assertEquals(34l, statTenant2.getItemPerCart().longValue());
		assertEquals(27l, statTenant2.getItemPurchased().longValue());
		assertEquals(3l, statTenant2.getUniqueVisitor().longValue());
		assertEquals(3l, statTenant2.getOrders().longValue());
	}
	
	@Test(expected=RuntimeException.class)
	public void aggregateFileWithInvalidTimeMetadata() throws IOException {
		File file = new File(getClass().getResource("/sample_invalid_metadata.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		pullService.aggregate(sources, LocalDateTime.parse("2015-06-19T03:00:00"));		
	}
	
}
