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
import com.predictry.fisher.domain.item.TopScoreType;
import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.tapirus.GetRecordsResult;
import com.predictry.fisher.domain.tapirus.GetRecordsResult.STATUS;
import com.predictry.fisher.domain.tapirus.RecordFile;
import com.predictry.fisher.domain.util.Helper;
import com.predictry.fisher.repository.LiveConfiguration;
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
	
	@Autowired
	private LiveConfiguration liveConfiguration;
	
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
	 * Update information on pull time.
	 * @param pullTime an instance of <code>PullTime</code> with new value.
	 * @return the updated <code>PullTime</code>.
	 */
	public PullTime update(PullTime pullTime) {
		PullTime existingPullTime = pullTimeRepository.findOne(pullTime.getId());
		if (existingPullTime != null) {
			existingPullTime.setForTime(pullTime.getForTime());
			existingPullTime = pullTimeRepository.index(existingPullTime);
		}
		return existingPullTime;
	}
	
	/**
	 * Process aggregation.
	 * 
	 * @param pullTime a <code>PullTime</code> to process.
	 */
	public void processAggregation(PullTime pullTime) {
		if (!liveConfiguration.isPullEnabled()) return;
		if ((pullTime != null) && pullTime.getForTime().isBefore(LocalDateTime.now())) {
			log.info("Processing aggregation for " + pullTime);
			try {
				GetRecordsResult tapResult = processAggregation(pullTime.getForTime());
				if (tapResult == null) {
					pullTime.fail();
				} else if (tapResult.getStatus() == STATUS.NOT_FOUND) {
					pullTime.success();
				} else if (tapResult.getStatus() == STATUS.PROCESSED) {
					pullTime.success();
				} else {
					pullTime.fail();
				}
				pullTimeRepository.save(pullTime);
			} catch (IOException e) {
				log.error("Error while processing aggregation.", e);
				pullTime.fail();
			}
			log.info("Done processing aggregation. Sleeping.");
		}
	}
	
	/**
	 * Process aggregation.
	 * @param time the time to get.
	 * @return an instance of <code>GetRecordsResult</code> or </code>null</code> if nothing is found.
	 * @throws IOException 
	 */
	public GetRecordsResult processAggregation(LocalDateTime time) throws IOException {
		GetRecordsResult tapResult = tapirusService.getRecords(time);
		if (tapResult != null) {
			if (tapResult.getStatus() == STATUS.PROCESSED) {
				ViewsAggregation viewsAggr = new ViewsAggregation();
				SalesAggregation salesAggr = new SalesAggregation();
				
				// Caculate and save stats
				log.info("Calculating stats for [" + time + "]");
				for (RecordFile recordFile: tapResult.getRecordFiles()) {
					List<String> data = tapirusService.readFile(recordFile);
					Map<String, Stat> stats = aggregate(data, time, viewsAggr, salesAggr);
					for (Stat stat: stats.values()) {
						statService.save(stat);
					}
				}
				log.info("Finish calculating stat.");
								
				// Calculate and save top scores
				log.info("Calculating top score for [" + time + "]");
				List<TopScore> topScores = topScore(viewsAggr, salesAggr, time);
				topScores.forEach(topScore -> topScoreService.save(topScore));
				log.info("Finish calculating top score.");
			}
		}
		return tapResult;
	}
	
	/**
	 * Process aggregation for default <code>PullTime</code>.
	 */
	public void processAggregation() {
		processAggregation(getDefaultPullTime());
	}
	
	public List<TopScore> topScore(ViewsAggregation viewsAggr, SalesAggregation salesAggr, LocalDateTime expectedTime) {
		List<TopScore> results = new ArrayList<>();
		
		// Top score for hits
		ScoreStore topScoreForViews = viewsAggr.getScoreStore();
		topScoreForViews.getData().forEach((tenantId, itemScores) -> {
			if (!liveConfiguration.isBlacklist(tenantId)) {
	 			TopScore topScore = new TopScore(TopScoreType.HIT);
				topScore.setTenantId(tenantId);
				topScore.setTime(expectedTime);
				itemScores.forEach(i -> {
					topScore.addNewScore(i);
				});
				results.add(topScore);
			}
		});
		
		// Top score for sales
		ScoreStore topScoreForSales = salesAggr.getScoreStore();
		topScoreForSales.getData().forEach((tenantId, itemScores) -> {
			if (!liveConfiguration.isBlacklist(tenantId)) {
	 			TopScore topScore = new TopScore(TopScoreType.SALES);
				topScore.setTenantId(tenantId);
				topScore.setTime(expectedTime);
				itemScores.forEach(i -> {
					topScore.addNewScore(i);
				});
				results.add(topScore);
			}
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
			} else {
				// Skip tenant id if it is blacklisted
				Map<String,Object> data = (Map<String,Object>) mapJson.get("data");
				String tenantId = (String) data.get("tenant");
				if (liveConfiguration.isBlacklist(tenantId)) {
					log.info("Skip blacklisted tenant: [" + tenantId + "]");
				} else {
					if (type.equals("Item")) {
						saveItem(mapJson);
					} else {
						for (Aggregation aggr: aggrs) {
							aggr.consume(mapJson, getStat(tenantId, expectedTime, results));
						}
					}	
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
		if ((fields != null) && (!fields.isEmpty()) && (fields.get("name") != null)) {
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
