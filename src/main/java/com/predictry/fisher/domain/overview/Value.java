package com.predictry.fisher.domain.overview;

public class Value<T> {

	private T overall;
	private T recommended;
	private T regular;
	
	public Value() {}
	
	public Value(T overall, T recommended, T regular) {
		this.overall = overall;
		this.recommended = recommended;
		this.regular = regular;
	}
	
	public T getOverall() {
		return overall;
	}
	
	public void setOverall(T overall) {
		this.overall = overall;
	}
	
	public T getRecommended() {
		return recommended;
	}
	
	public void setRecommended(T recommended) {
		this.recommended = recommended;
	}
	
	public T getRegular() {
		return regular;
	}
	
	public void setRegular(T regular) {
		this.regular = regular;
	}
		
}
