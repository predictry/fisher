package com.predictry.fisher.domain.aggregation;

import java.util.Map;
import static com.predictry.fisher.domain.util.Helper.*;
import com.predictry.fisher.domain.stat.Stat;

public class ViewsAggregation implements Aggregation {
	
	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("VIEW")) {
			stat.addViews(1l);
		}
	}

}
