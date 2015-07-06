package com.predictry.fisher.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class LiveConfiguration {

	private List<String> blacklistTenants = new ArrayList<>();
	
	private boolean pullEnabled = true;

	public List<String> getBlacklistTenants() {
		return blacklistTenants;
	}
	
	public boolean isBlacklist(String tenantId) {
		return blacklistTenants.contains(tenantId);
	}
	
	public void clearBlacklist() {
		blacklistTenants.clear();
	}
 
	public void setBlacklistTenants(String value) {
		blacklistTenants = new ArrayList<>();
		blacklistTenants.addAll(Arrays.asList(value.split(",")));
	}

	public boolean isPullEnabled() {
		return pullEnabled;
	}

	public void setPullEnabled(boolean pullEnabled) {
		this.pullEnabled = pullEnabled;
	}
	
}
