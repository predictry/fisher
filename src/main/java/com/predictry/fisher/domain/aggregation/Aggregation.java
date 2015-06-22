package com.predictry.fisher.domain.aggregation;

import java.util.Map;

import com.predictry.fisher.domain.stat.Stat;

public interface Aggregation {

	public void consume(Map<String, Object> mapJson, Stat stat);
	
	public default String getType(Map<String, Object> mapJson) {
		return (String) mapJson.get("type");
	}
	
	@SuppressWarnings("unchecked")
	public default Map<String, Object> getData(Map<String, Object> mapJson) {
		return (Map<String, Object>) mapJson.get("data");
	}
	
	public default String getDataName(Map<String,Object> mapJson) {
		return (String) getData(mapJson).get("name");
	}
	
}
