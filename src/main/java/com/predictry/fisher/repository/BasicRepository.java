package com.predictry.fisher.repository;

import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
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
