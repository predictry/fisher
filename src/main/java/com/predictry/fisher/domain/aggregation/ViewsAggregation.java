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

public class ViewsAggregation implements Aggregation {
	
	private ScoreStore scoreStore = new ScoreStore();
	
	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("VIEW")) {
			stat.addViews(new Value(1.0, isRecommended(mapJson)? 1.0: 0.0, 0.0));
		
			// Add views per item
			Map<String,Object> data = getData(mapJson);
			String tenantId = data.get("tenant").toString();
			String itemId = data.get("item").toString();
			scoreStore.add(tenantId, new ItemScore(itemId, "", "", 1.0));
		}
	}
	
	public ScoreStore getScoreStore() {
		return scoreStore;
	}
	
}
