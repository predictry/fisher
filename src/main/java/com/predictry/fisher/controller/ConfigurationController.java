package com.predictry.fisher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.config.LiveConfiguration;

@RestController
public class ConfigurationController {

	@Autowired
	private LiveConfiguration liveConfiguration;
	
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
		liveConfiguration.setBlacklistTenants(value);
	}
	
	@RequestMapping(value="/config/blacklist_tenants", method=RequestMethod.DELETE)
	public void clearBlacklistTenants() {
		liveConfiguration.clearBlacklist();
	}
	
	@RequestMapping("/config/pull_enabled")
	public boolean getPull() {
		return liveConfiguration.isPullEnabled();
	}
	
	@RequestMapping(value="/config/pull_enabled/{value}", method=RequestMethod.PUT)
	public void pullEnabled(@PathVariable boolean value) {
		liveConfiguration.setPullEnabled(value);
	}
		
}
