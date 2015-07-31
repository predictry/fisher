package com.predictry.fisher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ErrorLogService {

	private static final Logger log = LoggerFactory.getLogger(ErrorLogService.class);
	
	@JmsListener(destination="TAPIRUS.ERROR_LOG")
	public void processErrorLog(String data) {
		log.info("RECEIVING: " + data);
	}
	
}
