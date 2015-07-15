package com.predictry.fisher.domain.stat;

public enum ValueType {

	OVERALL("overall"), RECOMMENDED("recommended"), REGULAR("regular");
	
	private String keyword;
	
	private ValueType(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	@Override
	public String toString() {
		return keyword;
	}
	
}
