package com.predictry.fisher.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.item.Item;

@Service
@Transactional
public class ItemService {

	@Autowired
	private ElasticsearchOperations template;
	
	/**
	 * Save a new item into database.
	 * 
	 * @param item an <code>Item</code> to save.
	 */
	public void save(Item item) {
		String indexName = item.getIndexName();
		if (!template.indexExists(indexName)) {
			template.createIndex(indexName);
		}
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setIndexName(item.getIndexName());
		indexQuery.setType("item");
		indexQuery.setObject(item);
		indexQuery.setId(item.getId());
		template.index(indexQuery);
	}
	
}
