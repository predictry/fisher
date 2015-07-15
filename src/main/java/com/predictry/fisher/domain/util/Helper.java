package com.predictry.fisher.domain.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;
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
	
	/**
	 * Get an <code>"type"</code> from Tapirus' response.
	 * 
	 * @param mapJson a JSON map that represents result from Tapirus.
	 * @return the value for <code>"type"</code> key.
	 */
	public static String getType(Map<String, Object> mapJson) {
		return (String) mapJson.get("type");
	}

	/**
	 * Get the <code>"data"</code> section from Tapirus' response.
	 * 
	 * @param mapJson a JSON map that represents result from Tapirus.
	 * @return the <code>"data"</code> key.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getData(Map<String, Object> mapJson) {
		return (Map<String, Object>) mapJson.get("data");
	}
	
	/**
	 * Get the <code>"name"</code> value for <code>"data"</code> in Tapirus' response.
	 * 
	 * @param mapJson a JSON map that represents result from Tapirus.
	 * @return the <code>"name"</code> inside <code>"data"</code> section.
	 */
	public static String getDataName(Map<String,Object> mapJson) {
		return (String) getData(mapJson).get("name");
	}
	
	/**
	 * Check if JSON map has a flag that indicates it is an recommendation.
	 * 
	 * @param mapJson a JSON map that represents result from Tapirus.
	 * @return <code>true</code> if the map contains recommendation flag.
	 */
	@SuppressWarnings("unchecked")
	public static boolean isRecommended(Map<String,Object> mapJson) {
		if (mapJson.containsKey("recommendation")) {
			return (boolean) ((Map<String,Object>) mapJson.get("recommendation")).get("recommended");
		} else {
			return false;
		}
	}
	
	/**
	 * A temporary solution to do tenant id remapping.
	 * 
	 * @param oldTenantId the tenant id to remap.
	 * @return new tenant id.
	 */
	public static String tenantIdRemapping(String oldTenantId) {
		if (oldTenantId.equalsIgnoreCase("bukalapak")) {
			return "bukalapak";
		}
		if (oldTenantId.equalsIgnoreCase("familynara2014")) {
			return "FAMILYNARA2014";
		}
		return oldTenantId;
	}
	
	/**
	 * Convert an time from a time zone information to a different time zone.
	 * 
	 * @param time the time to convert.
	 * @param sourceTimeZone source time zone.
	 * @param timeZoneId destination time zone.
	 * @return the same time in destination time zone.
	 */
	public static LocalDateTime convertTimeZone(LocalDateTime time, String sourceTimeZone, String destinationTimeZone) {
		return time.atZone(ZoneId.of(sourceTimeZone)).withZoneSameInstant(ZoneId.of(destinationTimeZone)).toLocalDateTime();
	}
	
}
