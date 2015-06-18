package com.predictry.fisher.domain.stat;


public class StatEntry<T> {

	private String period;
	private T value;
	
	public StatEntry() {}
	
	public StatEntry(String period, T value) {
		super();
		this.period = period;
		this.value = value;
	}

	public String getPeriod() {
		return period;
	}
	
	public void setPeriod(String period) {
		this.period = period;
	}
		
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
}
