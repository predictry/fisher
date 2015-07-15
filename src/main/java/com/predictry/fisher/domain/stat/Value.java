package com.predictry.fisher.domain.stat;

import org.apache.http.util.Asserts;

public class Value {

	private Double overall = 0.0;
	private Double recommended = 0.0;
	private Double regular = 0.0;
	
	public Value() {}
	
	public Value(Double overall, Double recommended, Double regular) {
		this.overall = overall;
		this.recommended = recommended;
		this.regular = regular;
	}
	
	public Double getOverall() {
		return overall;
	}
	
	public void setOverall(Double overall) {
		this.overall = overall;
	}
	
	public Double getRecommended() {
		return recommended;
	}
	
	public void setRecommended(Double recommended) {
		this.recommended = recommended;
	}
	
	public Double getRegular() {
		return regular;
	}
	
	public void setRegular(Double regular) {
		this.regular = regular;
	}
	
	public Value plus(Value anotherValue) {
		Asserts.notNull(anotherValue, "Value to be added can't be null.");
		Value newValue = new Value();
		newValue.setOverall(this.overall + anotherValue.getOverall());
		newValue.setRecommended(this.recommended + anotherValue.getRecommended());
		newValue.setRegular(this.regular + anotherValue.getRecommended());
		return newValue;
	}
	
	public Value divide(Value anotherValue) {
		Asserts.notNull(anotherValue, "Dividend can't be null.");
		Value newValue = new Value();
		newValue.setOverall((anotherValue.getOverall() != 0) ? (this.overall / anotherValue.getOverall()) : 0.0);
		newValue.setRecommended((anotherValue.getRecommended() != 0) ? (this.recommended / anotherValue.getRecommended()) : 0.0);
		newValue.setRegular((anotherValue.getRegular() != 0) ? (this.regular / anotherValue.getRegular()) : 0.0);
		return newValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((overall == null) ? 0 : overall.hashCode());
		result = prime * result
				+ ((recommended == null) ? 0 : recommended.hashCode());
		result = prime * result + ((regular == null) ? 0 : regular.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Value other = (Value) obj;
		if (overall == null) {
			if (other.overall != null)
				return false;
		} else if (!overall.equals(other.overall))
			return false;
		if (recommended == null) {
			if (other.recommended != null)
				return false;
		} else if (!recommended.equals(other.recommended))
			return false;
		if (regular == null) {
			if (other.regular != null)
				return false;
		} else if (!regular.equals(other.regular))
			return false;
		return true;
	}
	
}
