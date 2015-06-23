package com.predictry.fisher.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.domain.aggregation.Aggregation;
import com.predictry.fisher.domain.aggregation.ItemPerCartAggregation;
import com.predictry.fisher.domain.aggregation.ItemPurchasedAggregation;
import com.predictry.fisher.domain.aggregation.SalesAggregation;
import com.predictry.fisher.domain.aggregation.UniqueVisitorAggregation;
import com.predictry.fisher.domain.aggregation.ViewsAggregation;
import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.repository.PullTimeRepository;

@Service
@Transactional
public class PullService {

	@Autowired
	private PullTimeRepository pullTimeRepository;
	
	private static final Logger log = LoggerFactory.getLogger(PullService.class);
	
	/**
	 * @return retrieve default <code>PullTime</code> for default task.
	 */
	public PullTime getDefaultPullTime() {
		return pullTimeRepository.findOne("default");
	}
	
	/**
	 * Process aggregation.
	 * 
	 * @param pullTime a <code>PullTime</code> to process.
	 */
	public void processAggregation(PullTime pullTime) {
		// TODO: Do pull operation from Tapirus here later!
	}
	
	/**
	 * Process aggregation for default <code>PullTime</code>.
	 */
	public void processAggregation() {
		processAggregation(getDefaultPullTime());
	}
	
	/**
	 * Perform the aggregation process for each line in data.
	 * 
	 * @param sources is the strings retrieved from data source.
	 * @param expectedTime is the expected time for this data source.  This field is used for validation.
	 * @return a <code>Map</code> that contains <code>Stat</code> for each tenant ids.
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Stat> aggregate(List<String> sources, LocalDateTime expectedTime) throws IOException {
		Map<String,Stat> results = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		// Define aggregation commands
		// Don't forget to register here everytime new aggregation command is created.
		List<Aggregation> aggrs = new ArrayList<>();
		aggrs.add(new ViewsAggregation());
		aggrs.add(new SalesAggregation());
		aggrs.add(new ItemPerCartAggregation());
		aggrs.add(new ItemPurchasedAggregation());
		aggrs.add(new UniqueVisitorAggregation());
		
		for (String line: sources) {
			Map<String,Object> mapJson = objectMapper.readValue(line, new TypeReference<Map<String,Object>>() {});
			String type = (String) mapJson.get("type");
			if (type.equals("metadata")) {
				// Check if time metadata returned from Fisher is for the expected time.
				Map<String,Object> metadataMap = (Map<String,Object>) mapJson.get("metadata");
				LocalDateTime time = LocalDate.parse((String) metadataMap.get("date")).atStartOfDay();
				time = time.withHour((Integer) metadataMap.get("hour"));
				if (!time.isEqual(expectedTime)) {
					log.error("Time metadata check failed!");
					throw new RuntimeException("Fisher's time metadata (" + time + ") is not for the expected time (" + expectedTime + ")");
				} else {
					log.info("Time metadata check success!");
				}
			} else {
				Map<String,Object> data = (Map<String,Object>) mapJson.get("data");
				String tenantId = (String) data.get("tenant");
				for (Aggregation aggr: aggrs) {
					aggr.consume(mapJson, getStat(tenantId, expectedTime, results));
				}
			}
		}
		
		// Post calculation for every stats
		for (Stat stat: results.values()) {
			stat.calculateItemPerCart();
		}
		
		return results;
	}
	
	/**
	 * Create new <code>Stat</code> or returns existing if available.
	 */
	private Stat getStat(String tenantId, LocalDateTime expectedTime, Map<String,Stat> stats) {
		if (stats.containsKey(tenantId)) {
			return stats.get(tenantId);
		}
		Stat stat = new Stat();
		stat.setTenantId(tenantId);
		stat.setTimeFrom(expectedTime);
		stats.put(tenantId, stat);
		return stat;
	}
	
}
