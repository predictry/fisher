package com.predictry.fisher.domain.aggregation;

import static com.predictry.fisher.domain.util.Helper.getData;
import static com.predictry.fisher.domain.util.Helper.getDataName;
import static com.predictry.fisher.domain.util.Helper.getType;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.predictry.fisher.domain.stat.Stat;

public class DeleteItemAggregation implements Aggregation {
	
	private static final Logger log = LoggerFactory.getLogger(DeleteItemAggregation.class);

	@Override
	public void consume(Map<String, Object> mapJson, Stat stat) {
		if (getType(mapJson).equals("Action") && getDataName(mapJson).equals("DELETE_ITEM")) {
			Map<String,Object> fields = getData(mapJson);
			String item = (String) fields.get("item");
			String tenant = (String) fields.get("tenant");
			String timestamp = (String) fields.get("timestamp");
			log.info(String.format("Found DELETE_ITEM event: tenant [%s], item [%s], timestamp [%s]", tenant, item, timestamp));
		}
	}

	@Override
	public void postProcessing(Stat stat) {
		// Do nothing
	}

}
