package com.predictry.fisher.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.stat.Metric;
import com.predictry.fisher.domain.stat.StatEntry;
import com.predictry.fisher.domain.stat.StatOverview;
import com.predictry.fisher.domain.stat.ValueType;
import com.predictry.fisher.domain.util.Helper;
import com.predictry.fisher.service.StatService;

@RestController
public class StatController {
	
	private static final Logger log = LoggerFactory.getLogger(StatController.class);
	
	@Autowired
	private StatService statService;

	/**
	 * Retrieve the aggregation as single value.
	 * 
	 * @param tenantId the tenant id to search for, for example, "Bukalapak".
	 * @param startDate starting date for aggregation.
	 * @param endDate end date for aggregation.
	 * @param timeZone an optional time zone if the time zone is not in UTC.
	 * @return JSON value (<code>StatOverview</code>).
	 */
	@RequestMapping("/stat/overview")
	public StatOverview statOverview(@RequestParam String tenantId, 
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime startDate, 
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate,
			@RequestParam(required=false) String timeZone) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		if (timeZone != null) {
			startDate = Helper.convertTimeZone(startDate, timeZone, "Z");
			endDate = Helper.convertTimeZone(endDate, timeZone, "Z");
		}
		log.info("Processing stat overview for tenantId [" + tenantId + "], startDate = [" + 
				startDate + "], endDate = [" + endDate + "]" );
		return statService.overview(startDate, endDate, tenantId);		
	}
	
	/**
	 * Retrieve the aggregation as group of values.
	 * 
	 * @param tenantId the tenant id to search for, for example, "Bukalapak".
	 * @param startDate starting date for aggregation.
	 * @param endDate end date for aggregation.
	 * @param metric name of metric to return.
	 * @param interval is interval for aggregation.  Can be string like <code>"year"</code>, <code>"month"</code>, etc or
	 * 				   expression such as <code>"1.5h"</code>.
	 * @param timeZone an optional time zone if the time zone is not in UTC.
	 * @return Array of JSON value.
	 */
	@RequestMapping("/stat")
	public List<StatEntry> statIndividual(@RequestParam String tenantId,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime startDate,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate,
			@RequestParam Metric metric, @RequestParam String interval,
			@RequestParam(required=false) String timeZone,
			@RequestParam(required=false) ValueType valueType) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		if (timeZone != null) {
			startDate = Helper.convertTimeZone(startDate, timeZone, "Z");
			endDate = Helper.convertTimeZone(endDate, timeZone, "Z");
		}
		if (valueType == null) {
			valueType = ValueType.OVERALL;
		}
		log.info("Processing stat for tenantId [" + tenantId + "], startDate [" + startDate + "], endDate = [" +
			endDate + "], metric [" + metric + "], interval [" + interval + "], valueType [" + valueType + "]");
		Interval bucketInterval = null;
		if (interval.equals("year")) {
			bucketInterval = Interval.YEAR;
		} else if (interval.equals("quarter")) {
			bucketInterval = Interval.QUARTER;
		} else if (interval.equals("month")) {
			bucketInterval = Interval.MONTH;
		} else if (interval.equals("week")) {
			bucketInterval = Interval.WEEK;
		} else if (interval.equals("day")) {
			bucketInterval = Interval.DAY;
		} else if (interval.equals("hour")) {
			bucketInterval = Interval.HOUR;
		} else {
			bucketInterval = new Interval(interval);
		}
		List<StatEntry> stats = statService.stat(startDate, endDate, tenantId, metric, bucketInterval, valueType);
		if (timeZone != null) {
			for (StatEntry entry: stats) {
				entry.setDate(Helper.convertTimeZone(entry.getDate(), "Z", timeZone));
			}
		}
		return stats;
	}
	
	/**
	 * Retrieve the aggregation as group of values.
	 * 
	 * @param tenantId the tenant id to search for, for example, "Bukalapak".
	 * @param startDate starting date for aggregation.
	 * @param endDate end date for aggregation.
	 * @param metric name of metric to return.
	 * @param interval is interval for aggregation.  Can be string like <code>"year"</code>, <code>"month"</code>, etc or
	 * 				   expression such as <code>"1.5h"</code>.
	 * @param timeZone an optional time zone if the time zone is not in UTC.
	 * @return Object of Array of JSON value for every type.
	 */
	@RequestMapping("/stat/combined")
	public List<List<StatEntry>> statCombined(@RequestParam String tenantId,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime startDate,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate,
			@RequestParam Metric metric, @RequestParam String interval,
			@RequestParam(required=false) String timeZone) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		if (timeZone != null) {
			startDate = Helper.convertTimeZone(startDate, timeZone, "Z");
			endDate = Helper.convertTimeZone(endDate, timeZone, "Z");
		}
		log.info("Processing grouped stat for tenantId [" + tenantId + "], startDate [" + startDate + "], endDate = [" +
			endDate + "], metric [" + metric + "], interval [" + interval + "]");
		Interval bucketInterval = null;
		if (interval.equals("year")) {
			bucketInterval = Interval.YEAR;
		} else if (interval.equals("quarter")) {
			bucketInterval = Interval.QUARTER;
		} else if (interval.equals("month")) {
			bucketInterval = Interval.MONTH;
		} else if (interval.equals("week")) {
			bucketInterval = Interval.WEEK;
		} else if (interval.equals("day")) {
			bucketInterval = Interval.DAY;
		} else if (interval.equals("hour")) {
			bucketInterval = Interval.HOUR;
		} else {
			bucketInterval = new Interval(interval);
		}
		Map<String, List<StatEntry>> results = statService.statGrouped(startDate, endDate, tenantId, metric, bucketInterval);
		if (timeZone != null) {
			for (String key: results.keySet()) {
				for (StatEntry entry: results.get(key)) {
					entry.setDate(Helper.convertTimeZone(entry.getDate(), "Z", timeZone));
				}
			}
		}
		List<List<StatEntry>> arrResults = new ArrayList<>();
		arrResults.add(results.get("overall"));
		arrResults.add(results.get("regular"));
		return arrResults;
	}
			
}

