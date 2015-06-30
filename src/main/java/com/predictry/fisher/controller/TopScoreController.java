package com.predictry.fisher.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.ErrorMessage;
//import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.TopScore;
import com.predictry.fisher.domain.item.TopScoreType;
import com.predictry.fisher.service.TopScoreService;

@RestController
public class TopScoreController {
	
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
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate) {
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
			@RequestParam @DateTimeFormat(pattern="yyyyMMddHH") LocalDateTime endDate) {
		return topScoreService.topScore(startDate, endDate, tenantId, TopScoreType.SALES);
	}
	
	/**
	 * General error handler for this controller.
	 */
	@ExceptionHandler(value={Exception.class, RuntimeException.class})
	public ErrorMessage error(Exception ex) {
		ErrorMessage error = new ErrorMessage(ex.getMessage());
		return error;
	} 

}
