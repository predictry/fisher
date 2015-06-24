package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;

public class StatEntry {

	@JsonSerialize(using=JacksonTimeSerializer.class)
	@JsonDeserialize(using=JacksonTimeDeserializer.class)
	private LocalDateTime date;
	
	private Double value;
	
	public StatEntry() {}
	
	public StatEntry(LocalDateTime period, Double value) {
		this.date = period;
		this.value = value;
	}

	public LocalDateTime getDate() {
		return date;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
		
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
}
