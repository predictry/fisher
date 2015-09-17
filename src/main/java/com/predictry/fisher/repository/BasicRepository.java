package com.predictry.fisher.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;


/**
 * A repository that performs CRUD and search operations to Elasticsearch.
 * This repository is not tied to specific index or type.
 * 
 * @author jocki
 *
 */
@Repository
public class BasicRepository {

	private static final Logger LOG = LoggerFactory.getLogger(BasicRepository.class);
	
	@Autowired
	private Client client;

	/**
	 * Find all indexed documents.
	 * 
	 * @param indexName is the index name.
	 * @param type is the type name.
	 * @param size is number of results that should be returned.
	 * @param from is number of initial results that should be skipped.
	 * @param sortFields is name of fields used for sorting.
	 * @return a <code>List</code> that contains documents or empty <code>List</code> if nothing found.
	 */
	public List<Map<String, Object>> findAll(String indexName, String type, int size, int from, List<String> sortFields) {
		SearchRequestBuilder builder = client.prepareSearch(indexName)
			.setTypes(type)
			.setQuery(QueryBuilders.matchAllQuery())
			.setFrom(from)
			.setSize(size);
		if (sortFields != null) {
			sortFields.stream().forEach(s -> builder.addSort(s, SortOrder.ASC));
		}
		SearchResponse response = builder.execute().actionGet();	
		List<Map<String, Object>> results = new ArrayList<>();
		for (SearchHit searchHit: response.getHits().getHits()) {
			Map<String, Object> item = searchHit.getSource();
			if (item != null) {
				if (!item.containsKey("id")) {
					item.put("id", searchHit.getId());
				}
				results.add(item);
			}
		}
		return results;
	}
	
	/**
	 * Find similiar item based on existing item.
	 * 
	 * @param indexName is the index name.
	 * @param type is the type name.
	 * @param minTermFreq is the ES' minTermFreq.
	 * @param id is the id of the document to match for.
	 * @return a <code>List</code> that contains matched ids or 
	 *         empty <code>List</code> if nothing matches.
	 */
	public List<String> similiar(String indexName, String type, int minTermFreq, String id) {
		SearchResponse response = client.prepareSearch(indexName)
			.setTypes(type)
			.setQuery(QueryBuilders.moreLikeThisQuery().ids(id).minTermFreq(minTermFreq))
			.execute()
			.actionGet();
		List<String> results = new ArrayList<>();
		for (SearchHit searchHit: response.getHits().getHits()) {
			results.add(searchHit.getId());
		}
		return results;
	}
	
	/**
	 * Find similiar item based on certain string on one or more fields.
	 * 
	 * @param indexName is the index name.
	 * @param type is the type name.
	 * @param minTermFreq is the ES' minTermFreq.
	 * @param likeText is the text to search for.
	 * @param fields is the fields to searh for.
	 * @return a <code>List</code> that contains matched ids or
	 *         empty <code>List</code> if nothing matches.
	 */
	public List<String> similiar(String indexName, String type, int minTermFreq, String likeText, String... fields) {
		SearchResponse response = client.prepareSearch(indexName)
			.setTypes(type)
			.setQuery(QueryBuilders.moreLikeThisQuery(fields).likeText(likeText).minTermFreq(minTermFreq))
			.execute()
			.actionGet();
		List<String> results = new ArrayList<>();
		for (SearchHit searchHit: response.getHits().getHits()) {
			results.add(searchHit.getId());
		}
		return results;
	}
	
	/**
	 * Create a new index.
	 * 
	 * @param indexName is the new index name.
	 * @return <code>true</code> if the operation is acknowledged.
	 */
	public boolean createIndex(String indexName) {
		// Don't add new if index already exists
		IndicesExistsResponse existsResponse = client.admin().indices()
			.prepareExists(indexName).execute().actionGet();
		if (!existsResponse.isExists()) {
			CreateIndexResponse response = client.admin().indices()
				.prepareCreate(indexName).execute().actionGet();
			if (response.isAcknowledged()) {
				LOG.info("New index [" + indexName + "] is created");
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Save a new document or update it if it already exists.
	 * 
	 * @param index is the index name to store this document.
	 * @param type is the type name to store this document.
	 * @param id is the id of this document.
	 * @param json is the content of this document.
	 * @return the id of this document.
	 */
	public String saveOrUpdate(String index, String type, String id, Map<String, Object> json) {
		IndexRequestBuilder builder = client.prepareIndex(index, type);
		builder.setId(id);
		createIndex(index);
		IndexResponse response = builder.setSource(json).setRefresh(true).execute().actionGet();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Id [" + response.getId() + "] is " + (response.isCreated() ? "created" : "updated"));
		}
		return response.getId();
	}
	
	/**
	 * Save multiple of documents in one batch.
	 * 
	 * @param index is the index name to store this document.
	 * @param type is the type name to store this document.
	 * @param objects is content of the documents where key is the id of the document.
	 * @return error messages if they are exists or <code>null</code> if there is no failures.
	 */
	public void saveBatch(String index, String type, Map<String, Map<String, Object>> objects) {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		objects.forEach((id,json) -> {
			bulkRequest.add(client.prepareIndex(index, type, id).setSource(json));
		});
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			throw new RuntimeException(bulkResponse.buildFailureMessage());
		}
	}
	
	/**
	 * Delete a document based on its id.
	 * 
	 * @param index is the index name to store this document.
	 * @param type is the type name to store this document.
	 * @param id is the id of document to delete.
	 * @return <code>true</code> is delete is success.
	 */
	public boolean delete(String index, String type, String id) {
		DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
		if (response.isFound()) {
			LOG.info("Id [" + id + "] is deleted.");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Find a document based on its id.
	 * 
	 * @param index is the index name of the document.
	 * @param type is the type name of the document.
	 * @param id is the id of the document to search for.
	 * @return the content of the document with specified id or <code>null</code> if it is not found.
	 */
	public Map<String, Object> find(String index, String type, String id) {
		LOG.info("Searching for [" + id + "] in index [" + index + "] type [" + type + "]");
		GetResponse response = client.prepareGet(index, type, id).execute().actionGet();
		if (!response.isExists()) {
			return null;
		} else {
			Map<String, Object> result = response.getSourceAsMap();
			if (!result.containsKey("id")) {
				result.put("id", id);
			}
			return result;
		}
	}
	
	/**
	 * Check if an index is exists.
	 * 
	 * @param index is the index to check for.
	 * @return <code>true</code> if it is exists.
	 */
	public boolean exists(String index) {
		Assert.notNull(index, "index must be specified.");
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}
	
	/**
	 * Delete a whole index.
	 * 
	 * @param index is the index to delete.
	 * @return <code>true</code> if the deletion is acknowledged.
	 */
	public boolean deleteIndex(String index) {
		Assert.notNull(index, "index must be specified");
		LOG.info("Deleting index [" + index + "]");
		return client.admin().indices().prepareDelete(index).execute().actionGet()
			.isAcknowledged();
	}
	
	/**
	 * Count number of all documents in an index.
	 * 
	 * @param index is the index name.
	 * @param type is the type name.
	 * @return number of documents in this index and type.
	 */
	public long count(String index, String type) {
		CountResponse response = client.prepareCount(index)
			.execute().actionGet();
		return response.getCount();
	}
	
}
