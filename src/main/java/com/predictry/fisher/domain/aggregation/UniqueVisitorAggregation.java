package com.predictry.fisher.domain.aggregation;

import static com.predictry.fisher.domain.util.Helper.getData;
import static com.predictry.fisher.domain.util.Helper.getDataName;
import static com.predictry.fisher.domain.util.Helper.getType;
import static com.predictry.fisher.domain.util.Helper.isRecommended;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.Value;

public class UniqueVisitorAggregation implements Aggregation {

	private Set<String> existingSessions = new HashSet<>();
	private Set<String> existingRecommendedSessions = new HashSet<>();
	
	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("VIEW")) {
			Map<String,Object> data = getData(mapJson);
			String session = data.get("session").toString();
			double qtyOverall = 0.0, qtyRecommended = 0.0;
			if (!existingSessions.contains(session)) {
				qtyOverall = 1.0;
				existingSessions.add(session);
			}
			if (isRecommended(mapJson) && !existingRecommendedSessions.contains(session)) {
				qtyRecommended = 1.0;
				existingRecommendedSessions.add(session);
			}
			stat.addUniqueVisitor(new Value(qtyOverall, qtyRecommended, 0.0));
		}		
	}

}
