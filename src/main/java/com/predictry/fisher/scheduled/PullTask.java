package com.predictry.fisher.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.service.PullService;

@Component
public class PullTask {

	private static final Logger log = LoggerFactory.getLogger(PullTask.class);
	
	public static final String ENV_FISHER_PULL = "FISHER_PULL"; 
	
	@Autowired
	private PullService pullService;
	
	/**
	 * Periodically pull records from Tapirus.  To disable this pulling task while
	 * keeping the server running (so it can serve), set an environment variable
	 * with name <code>FISHER_PULL</code> with value <code>false</code>.
	 */
	@Scheduled(fixedRate=60000) @Async
	public void pullLog() {
		String flag = System.getenv(ENV_FISHER_PULL);
		if ((flag != null) && flag.toLowerCase().equals("false")) {
			log.info("Pull task disabled.  " + ENV_FISHER_PULL + " [" + flag + "]");
			return;
		}
		PullTime pullTime = pullService.getDefaultPullTime();
		log.info("Processing aggregation for " + pullTime);
		pullService.processAggregation(pullTime);
		log.info("Done processing aggregation. Sleeping.");
	}
	
}
