package com.predictry.fisher.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.overview.StatOverview;
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
	 * @return JSON value (<code>StatOverview</code>).
	 */
	@RequestMapping("/stat/overview")
	public StatOverview statOverview(@RequestParam String tenantId, 
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime startDate, 
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate) {
		
		log.info("Processing stat overview for tenantId [" + tenantId + "], startDate = [" + 
				startDate + "], endDate = [" + endDate + "]" );
		return statService.overview(startDate, endDate, tenantId);		
	}
//	
//	/**
//	 * Retrieve the aggregation as group of values.
//	 * 
//	 * @param tenantId the tenant id to search for, for example, "Bukalapak".
//	 * @param startDate starting date for aggregation.
//	 * @param endDate end date for aggregation.
//	 * @param metric name of metric to return (such as "conversionRate", "itemPerCart", etc).
//	 * @param groupInHour aggregate per number of hour.
//	 * @return JSON value (<code>Stat</code>).
//	 */
//	@RequestMapping("/stat")
//	public Stat<?> stat(@RequestParam String tenantId,
//			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDate startDate,
//			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDate endDate,
//			@RequestParam String metric, @RequestParam int groupInHour ) {
//		Stat<Long> stat = new Stat<>(metric);
//		stat.addEntry(LocalDateTime.of(2015, 10, 1, 1, 0), 100l);
//		stat.addEntry(LocalDateTime.of(2015, 10, 1, 3, 0), 150l);
//		stat.addEntry(LocalDateTime.of(2015, 10, 1, 5, 0), 130l);
//		return stat;
//	}
//		
//	/**
//	 * General error handler for this controller.
//	 */
//	@ExceptionHandler(value={Exception.class, RuntimeException.class})
//	public ErrorMessage error(Exception ex) {
//		ErrorMessage error = new ErrorMessage(ex.getMessage());
//		return error;
//	} 
	
}

