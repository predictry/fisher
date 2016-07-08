package com.predictry.fisher.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.TopScore;
import com.predictry.fisher.domain.item.TopScoreType;
import com.predictry.fisher.domain.util.Helper;
import com.predictry.fisher.service.TopScoreService;

@RestController
@CrossOrigin
public class TopScoreController {
	
	private static final Logger log = LoggerFactory.getLogger(TopScoreController.class);
	
	@Autowired
	private TopScoreService topScoreService;
	
	/**
	 * Retrieve top 10 most viewed products.
	 * 
	 * @return JSON value (<code>TopScore</code>).
	 */
	@RequestMapping("/top/hits")
	public TopScore topView(@RequestParam String tenantId,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime startDate,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate,
			@RequestParam(required=false) String timeZone) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		if (timeZone != null) {
			startDate = Helper.convertTimeZone(startDate, timeZone, "Z");
			endDate = Helper.convertTimeZone(endDate, timeZone, "Z");
		}
		log.info("Processing top hits for tenantId [" + tenantId + "], startDate = [" + startDate + "], endDate = [" + endDate + "]" );
		return topScoreService.topScore(startDate, endDate, tenantId, TopScoreType.HIT);
	}
	
	/**
	 * Retrieve top 10 most sale products.
	 * 
	 * @return JSON value (<code>TopScore</code>).
	 */
	@RequestMapping("/top/sales")
	public TopScore topSales(@RequestParam String tenantId,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime startDate,
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate,
			@RequestParam(required=false) String timeZone) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		if (timeZone != null) {
			startDate = Helper.convertTimeZone(startDate, timeZone, "Z");
			endDate = Helper.convertTimeZone(endDate, timeZone, "Z");
		}
		log.info("Processing top sales for tenantId [" + tenantId + "], startDate = [" + startDate + "], endDate = [" + endDate + "]" );
		return topScoreService.topScore(startDate, endDate, tenantId, TopScoreType.SALES);
	}
	
}
