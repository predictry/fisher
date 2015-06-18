package com.predictry.fisher.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class PullLogTask {

	private static final Logger log = LoggerFactory.getLogger(PullLogTask.class);
	
	@Autowired
	//private ItemRepository topScoreRepository;
	
	@Scheduled(fixedRate=60000) @Async
	public void pullLog() {
		log.info("Executing pull log tasks...");
	}
	
}
