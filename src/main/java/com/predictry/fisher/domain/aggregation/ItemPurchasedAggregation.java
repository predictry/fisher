package com.predictry.fisher.domain.aggregation;

import java.util.Map;
import static com.predictry.fisher.domain.util.Helper.*;
import com.predictry.fisher.domain.stat.Stat;

public class ItemPurchasedAggregation implements Aggregation {

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> fields = (Map<String,Object>) getData(mapJson).get("fields");
			if (fields.containsKey("quantity")) {
				stat.addItemPurchased(Long.parseLong(fields.get("quantity").toString()));
			} else {
				stat.addItemPurchased(1l);
			}
		}				
	}

}
