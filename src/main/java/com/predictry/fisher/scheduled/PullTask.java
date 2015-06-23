package com.predictry.fisher.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.service.PullService;

@Component
public class PullTask {

	private static final Logger log = LoggerFactory.getLogger(PullTask.class);
	
	@Autowired
	private PullService pullService;
	
	@Scheduled(fixedRate=60000)
	public void pullLog() {
		PullTime pullTime = pullService.getDefaultPullTime();
		log.info("Processing aggregation for " + pullTime);
		pullService.processAggregation(pullTime);
		log.info("Done processing aggregation. Sleeping.");
	}
	
}
