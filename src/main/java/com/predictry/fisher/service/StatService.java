package com.predictry.fisher.service;

import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.stat.Metric;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.StatEntry;
import com.predictry.fisher.domain.stat.StatOverview;
import com.predictry.fisher.domain.stat.Value;
import com.predictry.fisher.domain.stat.ValueType;
import com.predictry.fisher.domain.util.Helper;

@Service
@Transactional
public class StatService {
	
	@Autowired
	private ElasticsearchOperations template;
	
	/**
	 * Save a new hourly <code>Stat</code> in Elasticsearch.  Depending on the time for this <code>stat</code>,
	 * Elasticsearch's index for that year will be created (such as, "stat_2014", "stat_2015", etc).
	 * 
	 * @param stat the <code>Stat</code> to save.
	 */
	public void save(Stat stat) {
		String indexName = stat.getIndexName();
		if (!template.indexExists(indexName)) {
			template.createIndex(indexName);
		}
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setIndexName(stat.getIndexName());
		indexQuery.setType(stat.getTenantId());
		indexQuery.setObject(stat);
		indexQuery.setId(stat.getTime().toString());
		template.index(indexQuery);
	}
	
	/**
	 * Calculate overall statictic for a period.
	 * 
	 * @param startTime is the starting date for this period.
	 * @param endTime is the ending date for this period.
	 * @param tenantId is the tenant id to calculate.
	 * @return an instance of <code>StatOverview</code>.
	 */
	public StatOverview overview(LocalDateTime startTime, LocalDateTime endTime, String tenantId) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
			.withIndices(Helper.convertToIndices("stat", startTime, endTime))
			.withTypes(tenantId)
			.withQuery(new FilteredQueryBuilder(null, rangeFilter("time").from(startTime).to(endTime)))
			.addAggregation(AggregationBuilders.sum("views.overall").field("views.overall"))
			.addAggregation(AggregationBuilders.sum("views.recommended").field("views.recommended"))
			.addAggregation(AggregationBuilders.sum("views.regular").field("views.regular"))
			.addAggregation(AggregationBuilders.sum("sales.overall").field("sales.overall"))
			.addAggregation(AggregationBuilders.sum("sales.recommended").field("sales.recommended"))
			.addAggregation(AggregationBuilders.sum("sales.regular").field("sales.regular"))
			.addAggregation(AggregationBuilders.sum("itemPurchased.overall").field("itemPurchased.overall"))
			.addAggregation(AggregationBuilders.sum("itemPurchased.recommended").field("itemPurchased.recommended"))
			.addAggregation(AggregationBuilders.sum("itemPurchased.regular").field("itemPurchased.regular"))
			.addAggregation(AggregationBuilders.sum("uniqueVisitor.overall").field("uniqueVisitor.overall"))
			.addAggregation(AggregationBuilders.sum("uniqueVisitor.recommended").field("uniqueVisitor.recommended"))
			.addAggregation(AggregationBuilders.sum("uniqueVisitor.regular").field("uniqueVisitor.regular"))
			.addAggregation(AggregationBuilders.sum("orders.overall").field("orders.overall"))
			.addAggregation(AggregationBuilders.sum("orders.recommended").field("orders.recommended"))
			.addAggregation(AggregationBuilders.sum("orders.regular").field("orders.regular"))
			.addAggregation(AggregationBuilders.sum("uniqueItemPurchased.overall").field("uniqueItemPurchased.overall"))
			.addAggregation(AggregationBuilders.sum("uniqueItemPurchased.recommended").field("uniqueItemPurchased.recommended"))
			.addAggregation(AggregationBuilders.sum("uniqueItemPurchased.regular").field("uniqueItemPurchased.regular"))
			.addAggregation(AggregationBuilders.avg("cartBoost").field("cartBoost"))
			.build();
		Aggregations aggregations = template.query(searchQuery, SearchResponse::getAggregations);

		StatOverview overview = new StatOverview();
		overview.setPageView(getAggregationValue(aggregations, "views"));
		overview.setSalesAmount(getAggregationValue(aggregations, "sales"));
		overview.setItemPurchased(getAggregationValue(aggregations, "itemPurchased"));
		overview.setUniqueVisitor(getAggregationValue(aggregations, "uniqueVisitor"));
		overview.setOrders(getAggregationValue(aggregations, "orders"));
		overview.setUniqueItemPurchased(getAggregationValue(aggregations, "uniqueItemPurchased"));
		overview.setCartBoost(((InternalAvg) aggregations.get("cartBoost")).getValue());
		return overview;
	}
	
	private Value getAggregationValue(Aggregations aggregations, String aggregationName) {
		double overall = ((InternalSum) aggregations.get(aggregationName + ".overall")).getValue();
		double recommended = ((InternalSum) aggregations.get(aggregationName + ".recommended")).getValue();
		double regular = ((InternalSum) aggregations.get(aggregationName + ".regular")).getValue();
		return new Value(overall, recommended, regular);
	}
	
	/**
	 * Calculate a specific metric for a period and group the result based on specified interval.
	 * 
	 * @param startTime is the starting date for the period.
	 * @param endTime is the ending date for the period.
	 * @param tenantId is the tenant id to calculate.
	 * @param metric is the metric to calculate.
	 * @param interval is the bucket interval.
	 * @param valueType is the type value to return.
	 * @return list of <code>StatEntry</code> for every bucket.
	 */
	public List<StatEntry> stat(LocalDateTime startTime, LocalDateTime endTime, String tenantId, 
			Metric metric, DateHistogram.Interval interval, ValueType valueType) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
			.withIndices(Helper.convertToIndices("stat", startTime, endTime))
			.withTypes(tenantId)
			.withQuery(new FilteredQueryBuilder(null, rangeFilter("time").from(startTime).to(endTime)))
			.addAggregation(
				AggregationBuilders.dateHistogram("aggr")
					.field("time")
					.interval(interval)
					.minDocCount(0L)
					.subAggregation(
						(metric == Metric.ITEM_PER_CART)?
						AggregationBuilders.avg("value").field(metric.getKeyword() + "." + valueType.getKeyword()):
						AggregationBuilders.sum("value").field(metric.getKeyword() + "." + valueType.getKeyword())))
			.build();
		Aggregations aggregations = template.query(searchQuery, SearchResponse::getAggregations);
		DateHistogram histogram = aggregations.get("aggr");
		List<StatEntry> result = new ArrayList<>();
		for (DateHistogram.Bucket bucket: histogram.getBuckets()) {
			LocalDateTime time = LocalDateTime.parse(bucket.getKey(), DateTimeFormatter.ISO_DATE_TIME);
			Double value = ((InternalNumericMetricsAggregation.SingleValue) bucket.getAggregations().get("value")).value();
			result.add(new StatEntry(time, value));
		}
		return result;
	}
	
	/**
	 * Calculate a specific metric for a period and group the result based on specified interval.
	 * This method will not filter the value based on type (such as recommended, overall, etc).
	 * 
	 * @param startTime is the starting date for the period.
	 * @param endTime is the ending date for the period.
	 * @param tenantId is the tenant id to calculate.
	 * @param metric is the metric to calculate.
	 * @param interval is the bucket interval.
	 * @return a <code>Map</code> that constains list of <code>StatEntry</code> for each value type.
	 */
	public Map<String, List<StatEntry>> statGrouped(LocalDateTime startTime, LocalDateTime endTime, String tenantId,
			Metric metric, DateHistogram.Interval interval) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
			.withIndices(Helper.convertToIndices("stat", startTime, endTime))
			.withTypes(tenantId)
			.withQuery(new FilteredQueryBuilder(null, rangeFilter("time").from(startTime).to(endTime)))
			.addAggregation(
				AggregationBuilders.dateHistogram("aggr")
					.field("time")
					.interval(interval)
					.minDocCount(0L)
					.subAggregation(
						(metric == Metric.ITEM_PER_CART)?
						AggregationBuilders.avg("value.overall").field(metric.getKeyword() + ".overall"):
						AggregationBuilders.sum("value.overall").field(metric.getKeyword() + ".overall"))
					.subAggregation(
						(metric == Metric.ITEM_PER_CART)?
						AggregationBuilders.avg("value.recommended").field(metric.getKeyword() + ".recommended"):
						AggregationBuilders.sum("value.recommended").field(metric.getKeyword() + ".recommended")))
			.build();
		Aggregations aggregations = template.query(searchQuery, SearchResponse::getAggregations);
		DateHistogram histogram = aggregations.get("aggr");
		List<StatEntry> overallValues = new ArrayList<>();
		List<StatEntry> recommendedValues = new ArrayList<>();
		List<StatEntry> regularValues = new ArrayList<>();
		for (DateHistogram.Bucket bucket: histogram.getBuckets()) {
			LocalDateTime time = LocalDateTime.parse(bucket.getKey(), DateTimeFormatter.ISO_DATE_TIME);
			Double overallValue = ((InternalNumericMetricsAggregation.SingleValue) bucket.getAggregations().get("value.overall")).value();
			Double recommendedValue = ((InternalNumericMetricsAggregation.SingleValue) bucket.getAggregations().get("value.recommended")).value();
			overallValues.add(new StatEntry(time, overallValue));
			recommendedValues.add(new StatEntry(time, recommendedValue));
			regularValues.add(new StatEntry(time, overallValue - recommendedValue));
		}
		
		Map<String, List<StatEntry>> results = new HashMap<>();
		results.put("overall", overallValues);
		results.put("recommended", recommendedValues);
		results.put("regular", regularValues);
		return results;
	}
	
}
