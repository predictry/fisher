package com.predictry.fisher.domain.aggregation;

import java.util.Map;
import static com.predictry.fisher.domain.util.Helper.*;
import com.predictry.fisher.domain.stat.Stat;

public class NumberOfSalesAggregation implements Aggregation {

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("BUY")) {
			stat.addOrder(1l);
		}		
	}

}
