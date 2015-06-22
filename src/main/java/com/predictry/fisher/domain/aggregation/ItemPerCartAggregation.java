package com.predictry.fisher.domain.aggregation;

import java.util.Map;

import com.predictry.fisher.domain.stat.Stat;

public class ItemPerCartAggregation implements Aggregation {

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("ADD_TO_CART")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> fields = (Map<String,Object>) getData(mapJson).get("fields");
			if (fields.containsKey("qty") && getData(mapJson).containsKey("session")) {
				stat.addItemPerCart((String) getData(mapJson).get("session"), Long.parseLong(fields.get("qty").toString()));
			}
		}		
	}

}
