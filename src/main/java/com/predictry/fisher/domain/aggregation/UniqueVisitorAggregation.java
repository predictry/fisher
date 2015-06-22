package com.predictry.fisher.domain.aggregation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.predictry.fisher.domain.stat.Stat;

public class UniqueVisitorAggregation implements Aggregation {

	private Set<String> existingIds = new HashSet<>();
	
	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("User") && getData(mapJson).containsKey("id")) {
			String id = (String) getData(mapJson).get("id");
			if (!existingIds.contains(id)) {
				stat.addUniqueVisitor(1l);
				existingIds.add(id);
			}
		}		
	}

}
