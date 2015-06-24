package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;

public class StatResultEntryDTO {

	@JsonSerialize(using=JacksonTimeSerializer.class)
	@JsonDeserialize(using=JacksonTimeDeserializer.class)
	private LocalDateTime period;
	
	private Double value;
	
	public StatResultEntryDTO() {}
	
	public StatResultEntryDTO(LocalDateTime period, Double value) {
		this.period = period;
		this.value = value;
	}

	public LocalDateTime getPeriod() {
		return period;
	}
	
	public void setPeriod(LocalDateTime period) {
		this.period = period;
	}
		
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
}
