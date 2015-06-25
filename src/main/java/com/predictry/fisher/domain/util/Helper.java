package com.predictry.fisher.domain.util;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Helper {

	/**
	 * Create an array of indices name to be used by Elasticsearch operations.  This is 
	 * usually used for logs store that uses indices such as 'stat_2015', 'stat_2014', etc.
	 * 
	 * @param prefix is a prefix for indices name.
	 * @param startTime is the start of period in question.
	 * @param endTime is the end of period in question.
	 * @return an array of indices names.
	 */
	public static String[] convertToIndices(String prefix, LocalDateTime startTime, LocalDateTime endTime) {
		Set<String> results = new HashSet<>();
		LocalDateTime time = startTime;
		while (!time.isAfter(endTime)) {
			results.add(prefix + "_" + time.getYear());
			time = time.plusYears(1);
		}
		return results.toArray(new String[]{});
	}
	
}
