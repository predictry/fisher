package com.predictry.fisher.domain.aggregation;

import static com.predictry.fisher.domain.util.Helper.getData;
import static com.predictry.fisher.domain.util.Helper.getDataName;
import static com.predictry.fisher.domain.util.Helper.getType;
import static com.predictry.fisher.domain.util.Helper.isRecommended;

import java.util.Map;

import com.predictry.fisher.domain.item.ItemScore;
import com.predictry.fisher.domain.item.ScoreStore;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.Value;

public class SalesAggregation implements Aggregation {
	
	private ScoreStore scoreStore = new ScoreStore();

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> fields = (Map<String,Object>) getData(mapJson).get("fields");
			if (fields.containsKey("sub_total") && !fields.get("sub_total").equals("null")) {
				Double subTotal = Double.parseDouble(fields.get("sub_total").toString());
				stat.addSales(new Value(
					subTotal,
					isRecommended(mapJson)? subTotal: 0.0,
					0.0
				));
				
				// Add sales amount per item
				Map<String,Object> data = getData(mapJson);
				String tenantId = data.get("tenant").toString();
				String itemId = data.get("item").toString();
				Double quantity = Double.parseDouble(fields.get("quantity").toString());
				scoreStore.add(tenantId, new ItemScore(itemId, "", "", quantity));
			}
		}		
	}
	
	public ScoreStore getScoreStore() {
		return scoreStore;
	}

	@Override
	public void postProcessing(Stat stat) {
	}

}
