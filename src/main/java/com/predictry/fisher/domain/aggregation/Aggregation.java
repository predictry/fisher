package com.predictry.fisher.domain.aggregation;

import java.util.Map;

import com.predictry.fisher.domain.stat.Stat;

public interface Aggregation {

	public void consume(Map<String, Object> mapJson, Stat stat);
	
	public void postProcessing(Stat stat);
	
}
