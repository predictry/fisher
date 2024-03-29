package com.predictry.fisher.domain.aggregation;

import java.util.Map;

import static com.predictry.fisher.domain.util.Helper.*;

import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.Value;

public class ItemPurchasedAggregation implements Aggregation {

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> fields = (Map<String,Object>) getData(mapJson).get("fields");
			double qty;
			if (fields.containsKey("quantity")) {
				try {
					qty = Double.parseDouble(fields.get("quantity").toString());
				} catch (NumberFormatException ex) {
					qty = 0.0;
				}
			} else {
				qty = 1;
			}
			stat.addItemPurchased(new Value(qty, isRecommended(mapJson)? qty: 0.0, 0.0));
		}				
	}

	@Override
	public void postProcessing(Stat stat) {
	}

}
