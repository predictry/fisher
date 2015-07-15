package com.predictry.fisher.domain.aggregation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.predictry.fisher.domain.util.Helper.*;

import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.Value;

public class UniqueVisitorAggregation implements Aggregation {

	private Set<String> existingIds = new HashSet<>();
	
	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("User") && getData(mapJson).containsKey("id")) {
			String id = (String) getData(mapJson).get("id");
			if (!existingIds.contains(id)) {
				stat.addUniqueVisitor(new Value(1.0, isRecommended(mapJson)?1.0:0.0, 0.0));
				existingIds.add(id);
			}
		}		
	}

}
