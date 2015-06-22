package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
		Map<String, Stat> stats = pullService.aggregate(file, LocalDateTime.parse("2015-06-19T03:00:00"));
		
		// Stat for 'tenant1'
		Stat statTenant1 = stats.get("tenant1");
		assertEquals(2l, statTenant1.getViews().longValue());
		
		// Stat for 'tenant2'
		Stat statTenant2 = stats.get("tenant2");
		assertEquals(3l, statTenant2.getViews().longValue());
	}
	
	@Test(expected=RuntimeException.class)
	public void aggregateFileWithInvalidTimeMetadata() throws IOException {
		File file = new File(getClass().getResource("/sample_invalid_metadata.log").getFile());
		pullService.aggregate(file, LocalDateTime.parse("2015-06-19T03:00:00"));		
	}
	
}
