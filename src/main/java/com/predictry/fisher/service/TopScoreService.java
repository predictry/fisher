package com.predictry.fisher.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.item.TopScore;

@Service
@Transactional
public class TopScoreService {

	@Autowired
	private ElasticsearchOperations template;
	
	/**
	 * Save a new hourly <code>TopScore</code> in Elasticsearch.  Depending on the time for this top score,
	 * Elasticsearch's index for that year will be created (such as, "top_view_2014", "top_view_2015", etc).
	 * 
	 * @param stat the <code>TopScore</code> to save.
	 */
	public void save(TopScore topScore) {
		String indexName = topScore.getIndexName();
		if (!template.indexExists(indexName)) {
			template.createIndex(indexName);
		}
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setIndexName(topScore.getIndexName());
		indexQuery.setType(topScore.getTenantId());
		indexQuery.setId(topScore.getTime().toString());
		indexQuery.setObject(topScore);
		template.index(indexQuery);
	}
	
}
