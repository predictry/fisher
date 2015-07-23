package com.predictry.fisher.domain.aggregation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.predictry.fisher.domain.util.Helper.*;

import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.Value;

public class UniqueItemPurchasedAggregation implements Aggregation {

	private Set<String> existingItems = new HashSet<>();
	private Set<String> existingRecommendedItems = new HashSet<>();
	
	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			Map<String,Object> data = getData(mapJson);
			String item = data.get("item").toString();
			double qtyOverall = 0.0, qtyRecommended = 0.0;
			if (!existingItems.contains(item)) {
				qtyOverall = 1.0;
				existingItems.add(item);
			}
			if (isRecommended(mapJson) && !existingRecommendedItems.contains(item)) {
				qtyRecommended = 1.0;
				existingRecommendedItems.add(item);
			}
			stat.addUniqueItemPurchased(new Value(qtyOverall, qtyRecommended, 0.0));
		}				
	}

}
