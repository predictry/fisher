package com.predictry.fisher.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.repository.LiveConfiguration;
import com.predictry.fisher.service.PullService;

@RestController
public class ConfigurationController {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);
	
	@Autowired
	private LiveConfiguration liveConfiguration;
	
	@Autowired
	private PullService pullService;
	
	@RequestMapping("/config")
	public LiveConfiguration get() {
		return liveConfiguration;
	}
	
	@RequestMapping("/config/blacklist_tenants")
	public List<String> getBlacklistTenants() {
		return liveConfiguration.getBlacklistTenants();
	}
	
	@RequestMapping(value="/config/blacklist_tenants/{value}", method=RequestMethod.PUT)
	public void blacklistTenants(@PathVariable String value) {
		log.info("New blacklisted tenants: [" + value + "]");
		liveConfiguration.setBlacklistTenants(value);
	}
	
	@RequestMapping(value="/config/blacklist_tenants", method=RequestMethod.DELETE)
	public void clearBlacklistTenants() {
		log.info("Tenants blacklist cleared.");
		liveConfiguration.clearBlacklist();
	}
	
	@RequestMapping("/config/pull_enabled")
	public boolean getPull() {
		return liveConfiguration.isPullEnabled();
	}
	
	@RequestMapping(value="/config/pull_enabled/{value}", method=RequestMethod.PUT)
	public void pullEnabled(@PathVariable boolean value) {
		log.info("New pull status: [" + value + "]");
		liveConfiguration.setPullEnabled(value);
	}
	
	@RequestMapping(value="/config/execution_time/{value}", method=RequestMethod.PUT)
	public void updateExecutionTime(@PathVariable String value) {
		PullTime pullTime = pullService.getDefaultPullTime();
		LocalDateTime time = LocalDateTime.parse(value); 
		log.info("New execution time: [" + time + "]");
		pullTime.setForTime(time);
		pullService.update(pullTime);
	}
	
	@RequestMapping("/config/execution_time")
	public LocalDateTime getExecutionTime() {
		return pullService.getDefaultPullTime().getForTime();
	}
		
}
