package com.predictry.fisher.domain.aggregation;

import static com.predictry.fisher.domain.util.Helper.getData;
import static com.predictry.fisher.domain.util.Helper.getDataName;
import static com.predictry.fisher.domain.util.Helper.getType;

import java.util.Map;

import com.predictry.fisher.domain.item.ScoreStore;
import com.predictry.fisher.domain.stat.Stat;

public class SalesAggregation implements Aggregation {
	
	private ScoreStore scoreStore = new ScoreStore();

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> fields = (Map<String,Object>) getData(mapJson).get("fields");
			if (fields.containsKey("sub_total")) {
				Double subTotal = Double.parseDouble(fields.get("sub_total").toString());
				stat.addSales(subTotal);
				
				// Add sales amount per item
//				Map<String,Object> data = getData(mapJson);
//				String tenantId = data.get("tenant").toString();
//				String itemId = data.get("item").toString();
//				// TODO: Add item information here later.
//				scoreStore.add(tenantId, new ItemScore(itemId, "XXX", "XXX", subTotal));
			}
		}		
	}
	
	public ScoreStore getScoreStore() {
		return scoreStore;
	}

}
