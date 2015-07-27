package com.predictry.fisher.domain.aggregation;

import static com.predictry.fisher.domain.util.Helper.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.Value;

public class NumberOfSalesAggregation implements Aggregation {
	
	Set<String> existingSession = new HashSet<>();

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			String session = (String) getData(mapJson).get("session");
			Value value = new Value(1.0, isRecommended(mapJson)?1.0:0.0, 0.0);
			if (session == null) {
				stat.addOrder(value);
			} else if (!existingSession.contains(session)) {
				stat.addOrder(value);
				existingSession.add(session);
			}
		}		
	}

	@Override
	public void postProcessing(Stat stat) {
	}

}
