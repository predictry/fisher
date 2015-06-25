package com.predictry.fisher.domain.aggregation;

import java.util.Map;
import static com.predictry.fisher.domain.util.Helper.*;
import com.predictry.fisher.domain.stat.Stat;

public class SalesAggregation implements Aggregation {

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> fields = (Map<String,Object>) getData(mapJson).get("fields");
			if (fields.containsKey("sub_total")) {
				stat.addSales(Double.parseDouble(fields.get("sub_total").toString()));
			}
		}		
	}

}
