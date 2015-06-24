package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatResultDTO {

	private Metric metric;
	private List<StatResultEntryDTO> entries = new ArrayList<>();
	
	public StatResultDTO(Metric metric) {
		this.metric = metric;
	}
	
	public Metric getMetric() {
		return metric;
	}
	
	public void setMetric(Metric metric) {
		this.metric = metric;
	}
	
	public List<StatResultEntryDTO> getEntries() {
		return entries;
	}
	
	public void addEntry(LocalDateTime time, Double value) {
		entries.add(new StatResultEntryDTO(time, value));
	}
	
}
