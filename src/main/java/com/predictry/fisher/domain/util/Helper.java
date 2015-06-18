package com.predictry.fisher.domain.util;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Helper {

	public static String[] convertToIndices(LocalDateTime startTime, LocalDateTime endTime) {
		Set<String> results = new HashSet<>();
		LocalDateTime time = startTime;
		while (!time.isAfter(endTime)) {
			results.add("stat_" + time.getYear());
			time = time.plusYears(1);
		}
		return results.toArray(new String[]{});
	}
}
