package com.predictry.fisher.service;

import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
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
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.util.Helper;

@Service
@Transactional
public class StatService {

	@Autowired
	private ElasticsearchOperations template;
	
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
	
	public StatOverview overview(LocalDateTime startTime, LocalDateTime endTime, String tenantId) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
			.withIndices(Helper.convertToIndices(startTime, endTime))
			.withTypes(tenantId)
			.withQuery(new FilteredQueryBuilder(null, rangeFilter("time").from(startTime).to(endTime)))
			.addAggregation(AggregationBuilders.sum("views").field("views"))
			.addAggregation(AggregationBuilders.sum("salesAmount").field("sales"))
			.addAggregation(AggregationBuilders.avg("itemPerCart").field("itemPerCart"))
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
		long itemPerCart = (long) ((InternalAvg) aggregations.get("itemPerCart")).getValue();
		long itemPurchased = (long) ((InternalSum) aggregations.get("itemPurchased")).getValue();
		double salesPerCart = ((InternalSum) aggregations.get("salesPerCart")).getValue();
		long uniqueVisitor = (long) ((InternalSum) aggregations.get("uniqueVisitor")).getValue();
		long order = (long) ((InternalSum) aggregations.get("orders")).getValue();
		StatOverview overview = new StatOverview();
		overview.setPageView(new Value<Long>(totalViews, 0l, 0l));
		overview.setSalesAmount(new Value<Double>(totalSalesAmount, 0.0, 0.0));
		overview.setItemPerCart(new Value<Long>(itemPerCart, 0l, 0l));
		overview.setItemPurchased(new Value<Long>(itemPurchased, 0l, 0l));
		overview.setSalesPerCart(new Value<Double>(salesPerCart, 0.0, 0.0));
		overview.setUniqueVisitor(new Value<Long>(uniqueVisitor, 0l, 0l));
		overview.setOrders(order);
		overview.setConversionRate(0.0);
		return overview;
	}
	
	
}
