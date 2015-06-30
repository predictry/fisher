package com.predictry.fisher.service;

import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.TopScore;
import com.predictry.fisher.domain.item.TopScoreType;
import com.predictry.fisher.domain.util.Helper;

@Service
@Transactional
public class TopScoreService {

	private static final Logger log = LoggerFactory.getLogger(TopScoreService.class);
	
	@Autowired
	private ElasticsearchOperations template;
	
	@Autowired
	private ItemService itemService;
	
	/**
	 * Save a new hourly <code>TopScore</code> in Elasticsearch.  Depending on the time for this top score,
	 * Elasticsearch's index for that year will be created (such as, "top_2014", "top_2015", etc).
	 * 
	 * @param stat the <code>TopScore</code> to save.
	 */
	public void save(TopScore topScore) {
		String indexName = topScore.getIndexName();
		if (!template.indexExists(indexName)) {
			template.createIndex(indexName);
		}
		if (!template.typeExists(indexName, topScore.getTenantId())) {
			try {
				//
				// Top index need  a custom mapping to mark 'items' field as nested.
				// Because of the custom indices naming, the @Field annotation doesn't work
				// and we need to build the mapping manually.
				//
				XContentBuilder xb = XContentFactory.jsonBuilder().startObject()
					.startObject("properties")
						.startObject("indexName")
							.field("type", "string")
						.endObject()
						.startObject("items")
							.field("type", "nested")
							.field("properties").startObject()
								.startObject("id")
									.field("type", "string")
								.endObject()
								.startObject("name")
									.field("type", "string")
								.endObject()
								.startObject("score")
									.field("type", "double")
								.endObject()
								.startObject("url")
									.field("type", "string")
								.endObject()
							.endObject()
						.endObject()
						.startObject("time")
							.field("type", "date")
							.field("format", "dateOptionalTime")
						.endObject()
					.endObject()
				.endObject();
				template.putMapping(indexName, topScore.getTenantId(), xb);
			} catch (IOException e) {
				log.error("Can't create custom mapping for top score index", e);
			}
		}	
		template.refresh(indexName, true);
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setIndexName(topScore.getIndexName());
		indexQuery.setType(topScore.getTenantId());
		indexQuery.setId(topScore.getTime().toString());
		indexQuery.setObject(topScore);
		template.index(indexQuery);
	}
	
	/**
	 * Retrieve most viewed items for a period for a tenant id.
	 * 
	 * @param startTime the start of period.
	 * @param endTime the end of period.
	 * @param tenantId the name of tenant id.
	 * @param type the type of the score to display.
	 * @return <code>TopScore</code> representing most viewed items.
	 */
	public TopScore topScore(LocalDateTime startTime, LocalDateTime endTime, String tenantId, TopScoreType type) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
			.withIndices(Helper.convertToIndices("top_" + type.getPrefix(), startTime, endTime))
			.withTypes(tenantId)
			.withQuery(new FilteredQueryBuilder(null, rangeFilter("time").from(startTime).to(endTime)))
			.addAggregation(AggregationBuilders.nested("items").path("items")
				.subAggregation(AggregationBuilders.terms("top_items").field("items.id").order(Order.aggregation("total", false)).size(10)
						.subAggregation(AggregationBuilders.sum("total").field("items.score"))))
			.build();
		Aggregations aggregations = template.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		Aggregations nestedAggrs = ((InternalNested) aggregations.get("items")).getAggregations();
		StringTerms topItems = nestedAggrs.get("top_items");
		TopScore topScore = new TopScore(TopScoreType.HIT);
		topScore.setTime(LocalDateTime.now());
		for (Bucket bucket: topItems.getBuckets()) {
			String id = bucket.getKey();
			double total = (double) ((InternalSum) bucket.getAggregations().get("total")).getValue();
			Item item = itemService.find(tenantId, id);
			topScore.addNewScore(id, (item==null)?"":item.getName(), (item==null)?"":item.getItemUrl(), total);
		}
		return topScore;
	}
		
}
