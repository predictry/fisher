package com.predictry.fisher.domain.util;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.*;

import org.junit.Test;

public class HelperTest {

	 @Test
	 public void convertToIndices() {
		 String[] result = Helper.convertToIndices(LocalDateTime.parse("2015-01-01T10:00:00"), 
			 LocalDateTime.parse("2015-01-01T12:00:00"));
		 assertEquals(1, result.length);
		 assertEquals("stat_2015", result[0]);
		 
		 result = Helper.convertToIndices(LocalDateTime.parse("2015-01-01T10:00:00"), 
			 LocalDateTime.parse("2015-01-02T11:00:00"));
		 assertEquals(1, result.length);
		 assertEquals("stat_2015", result[0]);
		
		 result = Helper.convertToIndices(LocalDateTime.parse("2014-01-01T10:00:00"), 
			 LocalDateTime.parse("2015-01-02T11:00:00"));
		 assertEquals(2, result.length);
		 assertTrue(Arrays.asList(result).contains("stat_2014"));
		 assertTrue(Arrays.asList(result).contains("stat_2015"));
		 
		 result = Helper.convertToIndices(LocalDateTime.parse("2013-01-01T10:00:00"),
			 LocalDateTime.parse("2015-01-02T11:00:00"));
		 assertEquals(3, result.length);
		 assertTrue(Arrays.asList(result).contains("stat_2013"));
		 assertTrue(Arrays.asList(result).contains("stat_2014"));
		 assertTrue(Arrays.asList(result).contains("stat_2015"));
	 }
	 
}
