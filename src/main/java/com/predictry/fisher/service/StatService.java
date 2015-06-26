package com.predictry.fisher.service;

import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.overview.StatOverview;
import com.predictry.fisher.domain.overview.Value;
import com.predictry.fisher.domain.stat.Metric;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.StatEntry;
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
		indexQuery.setId(stat.getTime());
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
			.addAggregation(AggregationBuilders.sum("views").field("views"))
			.addAggregation(AggregationBuilders.sum("salesAmount").field("sales"))
			.addAggregation(AggregationBuilders.sum("itemPurchased").field("itemPurchased"))
			.addAggregation(AggregationBuilders.sum("salesPerCart").field("orders"))
			.addAggregation(AggregationBuilders.sum("uniqueVisitor").field("uniqueVisitor"))
			.addAggregation(AggregationBuilders.sum("orders").field("orders"))
			.build();
		Aggregations aggregations = template.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		
		long totalViews = (long) ((InternalSum) aggregations.get("views")).getValue();
		double totalSalesAmount = (double) ((InternalSum) aggregations.get("salesAmount")).getValue();
		long itemPurchased = (long) ((InternalSum) aggregations.get("itemPurchased")).getValue();
		double salesPerCart = ((InternalSum) aggregations.get("salesPerCart")).getValue();
		long uniqueVisitor = (long) ((InternalSum) aggregations.get("uniqueVisitor")).getValue();
		long order = (long) ((InternalSum) aggregations.get("orders")).getValue();
		StatOverview overview = new StatOverview();
		overview.setPageView(new Value<Long>(totalViews, 0l, 0l));
		overview.setSalesAmount(new Value<Double>(totalSalesAmount, 0.0, 0.0));
		overview.setItemPurchased(new Value<Long>(itemPurchased, 0l, 0l));
		overview.setSalesPerCart(new Value<Double>(salesPerCart, 0.0, 0.0));
		overview.setUniqueVisitor(new Value<Long>(uniqueVisitor, 0l, 0l));
		overview.setOrders(order);
		return overview;
	}
	
	/**
	 * Calculate a specific metric for a period and group the result based on specified interval.
	 * 
	 * @param startTime is the starting date for the period.
	 * @param endTime is the ending date for the period.
	 * @param tenantId is the tenant id to calculate.
	 * @param metric is the metric to calculate.
	 * @param interval is the bucket interval.
	 * @return list of <code>StatEntry</code> for every bucket.
	 */
	public List<StatEntry> stat(LocalDateTime startTime, LocalDateTime endTime, String tenantId, 
			Metric metric, DateHistogram.Interval interval) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
			.withIndices(Helper.convertToIndices("stat", startTime, endTime))
			.withTypes(tenantId)
			.withQuery(new FilteredQueryBuilder(null, rangeFilter("time").from(startTime).to(endTime)))
			.addAggregation(
				AggregationBuilders.dateHistogram("aggr")
					.field("time")
					.interval(interval)
					.minDocCount(0l)
					.subAggregation(
						(metric == Metric.ITEM_PER_CART)?
						AggregationBuilders.avg("value").field(metric.getKeyword()):
						AggregationBuilders.sum("value").field(metric.getKeyword())))
			.build();
		Aggregations aggregations = template.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		DateHistogram histogram = aggregations.get("aggr");
		List<StatEntry> result = new ArrayList<>();
		for (DateHistogram.Bucket bucket: histogram.getBuckets()) {
			LocalDateTime time = LocalDateTime.parse(bucket.getKey(), DateTimeFormatter.ISO_DATE_TIME);
			Double value = ((InternalNumericMetricsAggregation.SingleValue) bucket.getAggregations().get("value")).value();
			result.add(new StatEntry(time, value));
		}
		return result;
	}
	
}
