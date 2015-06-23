package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.tapirus.GetRecordsResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class TapirusServiceTest {

	@Autowired
	private TapirusService tapirusService;
	
	@Test
	public void testGetRecords() {
		LocalDateTime time = LocalDateTime.parse("2015-06-01T01:00:00");
		GetRecordsResult result = tapirusService.getRecords(time);
		assertEquals(LocalDate.parse("2015-06-01"), result.getDate());
		assertEquals(1, result.getHour().intValue());
		assertEquals(GetRecordsResult.STATUS.PROCESSED, result.getStatus());
		assertEquals("trackings/records/2015/06/01/2015-06-01-01.gz", result.getUri());
		try {
			List<String> results = tapirusService.readFile(result);
			assertEquals(468608, results.size());
		} catch (IOException e) {
			fail("Exception: " + e.getMessage());
		}
	}
}
