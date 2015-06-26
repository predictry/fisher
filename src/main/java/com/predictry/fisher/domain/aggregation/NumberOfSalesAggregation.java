package com.predictry.fisher.domain.aggregation;

import static com.predictry.fisher.domain.util.Helper.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.predictry.fisher.domain.stat.Stat;

public class NumberOfSalesAggregation implements Aggregation {
	
	Set<String> existingSession = new HashSet<>();

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			String session = (String) getData(mapJson).get("session");
			if (session == null) {
				stat.addOrder(1l);
			} else if (!existingSession.contains(session)) {
				stat.addOrder(1l);
				existingSession.add(session);
			}
		}		
	}

}
