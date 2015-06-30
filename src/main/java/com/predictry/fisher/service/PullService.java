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
import com.predictry.fisher.domain.aggregation.ItemPurchasedAggregation;
import com.predictry.fisher.domain.aggregation.NumberOfSalesAggregation;
import com.predictry.fisher.domain.aggregation.SalesAggregation;
import com.predictry.fisher.domain.aggregation.UniqueVisitorAggregation;
import com.predictry.fisher.domain.aggregation.ViewsAggregation;
import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.ScoreStore;
import com.predictry.fisher.domain.item.TopScore;
import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.tapirus.GetRecordsResult;
import com.predictry.fisher.domain.tapirus.GetRecordsResult.STATUS;
import com.predictry.fisher.domain.util.Helper;
import com.predictry.fisher.repository.PullTimeRepository;

@Service
@Transactional
public class PullService {

	private static final Logger log = LoggerFactory.getLogger(PullService.class);
	
	@Autowired
	private PullTimeRepository pullTimeRepository;
	
	@Autowired
	private TapirusService tapirusService;
	
	@Autowired
	private StatService statService;
	
	@Autowired
	private TopScoreService topScoreService;
	
	@Autowired
	private ItemService itemService;
	
	/**
	 * @return retrieve default <code>PullTime</code> for default task.
	 */
	public PullTime getDefaultPullTime() {
		PullTime pullTime = pullTimeRepository.findOne("default");
		if (pullTime == null) {
			pullTime = new PullTime("default", LocalDateTime.parse("2015-01-01T00:00:00"));
		}
		return pullTime;
	}
	
	/**
	 * Process aggregation.
	 * 
	 * @param pullTime a <code>PullTime</code> to process.
	 */
	public void processAggregation(PullTime pullTime) {
		if ((pullTime != null) && pullTime.getForTime().isBefore(LocalDateTime.now())) {
			GetRecordsResult tapResult = tapirusService.getRecords(pullTime.getForTime());
			if (tapResult.getStatus() == STATUS.NOT_FOUND) {
				pullTime.success();
			} else if (tapResult.getStatus() == STATUS.PROCESSED) {
				// Process and save aggregation
				try {
					ViewsAggregation viewsAggr = new ViewsAggregation();
					SalesAggregation salesAggr = new SalesAggregation();
					List<String> data = tapirusService.readFile(tapResult);
					
					// Calculate and save stats
					Map<String, Stat> stats = aggregate(data, pullTime.getForTime(), viewsAggr, salesAggr);
					for (Stat stat: stats.values()) {
						statService.save(stat);
					}
					
					// Calculate and save top scores
					List<TopScore> topScores = topScore(viewsAggr, salesAggr, pullTime.getForTime());
					topScores.forEach(topScore -> topScoreService.save(topScore));
					
					pullTime.success();
				} catch (IOException e) {
					log.error("Error while processing aggregation.", e);
					pullTime.fail();
				}
			} else {
				pullTime.fail();
			}
			pullTimeRepository.save(pullTime);
		}
	}
	
	/**
	 * Process aggregation for default <code>PullTime</code>.
	 */
	public void processAggregation() {
		processAggregation(getDefaultPullTime());
	}
	
	public List<TopScore> topScore(ViewsAggregation viewsAggr, SalesAggregation salesAggr, LocalDateTime expectedTime) {
		List<TopScore> results = new ArrayList<>();
		
		// Top score
		ScoreStore topScoreForViews = viewsAggr.getScoreStore();
		topScoreForViews.getData().forEach((tenantId, itemScores) -> {
			TopScore topScore = new TopScore();
			topScore.setTenantId(tenantId);
			topScore.setTime(expectedTime);
			itemScores.forEach(i -> {
				topScore.addNewScore(i);
			});
			results.add(topScore);
		});
		
		return results;
	}
	
	/**
	 * Perform the aggregation process for each line in data.
	 * 
	 * @see #aggregate(List, LocalDateTime, ViewsAggregation, SalesAggregation)
	 */
	public Map<String,Stat> aggregate(List<String> sources, LocalDateTime expectedTime) throws IOException {
		return aggregate(sources, expectedTime, new ViewsAggregation(), new SalesAggregation());
	}
	
	/**
	 * Perform the aggregation process for each line in data.
	 * 
	 * @param sources is the strings retrieved from data source.
	 * @param expectedTime is the expected time for this data source.  This field is used for validation.
	 * @return a <code>Map</code> that contains <code>Stat</code> for each tenant ids.
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Stat> aggregate(List<String> sources, LocalDateTime expectedTime, ViewsAggregation viewsAggregation, SalesAggregation salesAggregation) throws IOException {
		Map<String,Stat> results = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		// Define aggregation commands
		// Don't forget to register here everytime new aggregation command is created.
		List<Aggregation> aggrs = new ArrayList<>();
		aggrs.add(viewsAggregation);
		aggrs.add(salesAggregation);
		aggrs.add(new ItemPurchasedAggregation());
		aggrs.add(new UniqueVisitorAggregation());
		aggrs.add(new NumberOfSalesAggregation());
		
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
			} else if (type.equals("Item")) {
				saveItem(mapJson);
			} else {
				Map<String,Object> data = (Map<String,Object>) mapJson.get("data");
				String tenantId = (String) data.get("tenant");
				for (Aggregation aggr: aggrs) {
					aggr.consume(mapJson, getStat(tenantId, expectedTime, results));
				}
			}
		}
				
		return results;
	}
	
	private void saveItem(Map<String,Object> mapJson) {
		Item item = new Item();
		item.setId((String) Helper.getData(mapJson).get("id"));
		item.setTenantId((String) Helper.getData(mapJson).get("tenant"));
		@SuppressWarnings("unchecked")
		Map<String,Object> fields = (Map<String, Object>) Helper.getData(mapJson).get("fields");
		if ((fields != null) && (!fields.isEmpty())) {
			item.setName((String) fields.get("name"));
			item.setImageUrl((String) fields.get("img_url"));
			item.setItemUrl((String) fields.get("item_url"));
			item.setCategory((String) fields.get("category"));
			itemService.save(item);
		}
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
