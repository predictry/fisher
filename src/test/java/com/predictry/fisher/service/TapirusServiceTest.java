package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import com.predictry.fisher.domain.tapirus.RecordFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class TapirusServiceTest {

	@Autowired
	private TapirusService tapirusService;
	
	@Test
	public void testGetRecords() {
		LocalDateTime time = LocalDateTime.parse("2015-07-13T09:00:00");
		GetRecordsResult result = tapirusService.getRecords(time);
		assertEquals(LocalDate.parse("2015-07-13"), result.getDate());
		assertEquals(9, result.getHour().intValue());
		assertEquals(GetRecordsResult.STATUS.PROCESSED, result.getStatus());
		RecordFile[] recordFiles = result.getRecordFiles();
		assertNotNull(recordFiles);
		assertTrue(recordFiles.length > 0);
		try {
			List<String> results = tapirusService.readFile(recordFiles[0]);
			assertNotNull(results);
			assertFalse(results.isEmpty());
		} catch (IOException e) {
			fail("Exception: " + e.getMessage());
		}
	}
}
