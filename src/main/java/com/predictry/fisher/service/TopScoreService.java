package com.predictry.fisher.service;

import java.io.IOException;

import javax.transaction.Transactional;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.item.TopScore;

@Service
@Transactional
public class TopScoreService {

	private static final Logger log = LoggerFactory.getLogger(TopScoreService.class);
	
	@Autowired
	private ElasticsearchOperations template;
	
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
	
}
