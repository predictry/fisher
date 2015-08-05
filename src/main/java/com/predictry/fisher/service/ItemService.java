package com.predictry.fisher.service;

import static org.elasticsearch.index.query.FilterBuilders.termFilter;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.http.util.Asserts;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.item.Item;

/**
 * This class is deprecated because we now save all information about
 * items which can be flexible, not just a certain properties defined
 * in <code>Item</code>.
 *  
 * @author jocki
 *
 */
@Service
@Transactional
@Deprecated
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
	
	/**
	 * Find an <code>Item</code>.
	 * 
	 * @param tenantId is the tenant id in which this item belongs to.
	 * @param itemId is the id to search for.
	 * @return an <code>Item</code> or <code>null</code> if nothing is found.
	 */
	public Item find(String tenantId, String itemId) {
		Asserts.notNull(tenantId, "Tenant id can't be null.");
		Asserts.notNull(itemId, "Item id can't be null.");
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("item_" + tenantId.toLowerCase())
			.withTypes("item")
			.withIds(Arrays.asList(itemId))
			.withQuery(new FilteredQueryBuilder(null, termFilter("id", itemId)))
			.build();
		List<Item> results = template.queryForList(searchQuery, Item.class);
		if (results.isEmpty()) {
			return null;
		} else {
			Item result = results.get(0);
			result.setTenantId(tenantId);
			return result;
		}
	}
	
	/**
	 * Count number of items owned by a tenant.
	 * 
	 * @param tenantId is the tenant id.
	 * @return number of items for this tenant id.
	 */
	public long count(String tenantId) {
		Asserts.notNull(tenantId, "Tenant id can't be null.");
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("item_" + tenantId.toLowerCase())
			.withTypes("item")
			.build();
		return template.count(searchQuery);
	}
	
}
