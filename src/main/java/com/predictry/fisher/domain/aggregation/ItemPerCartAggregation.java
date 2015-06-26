package com.predictry.fisher.domain.aggregation;

import java.util.Map;
import static com.predictry.fisher.domain.util.Helper.*;
import com.predictry.fisher.domain.stat.Stat;

public class ItemPerCartAggregation implements Aggregation {

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("ADD_TO_CART")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> fields = (Map<String,Object>) getData(mapJson).get("fields");
			if (fields.containsKey("quantity") && getData(mapJson).containsKey("session")) {
				stat.addItemPerCart((String) getData(mapJson).get("session"), Long.parseLong(fields.get("quantity").toString()));
			}
		}		
	}

}
