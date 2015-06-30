package com.predictry.fisher.domain.item;

public enum TopScoreType {

	HIT("hits"), SALES("sales");
	
	private String prefix;
	
	private TopScoreType(String prefix) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
}
