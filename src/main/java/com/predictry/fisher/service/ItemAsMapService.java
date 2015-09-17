package com.predictry.fisher.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.domain.item.ItemRecommendation;
import com.predictry.fisher.domain.util.JsonMessageCreator;
import com.predictry.fisher.repository.BasicRepository;

@Service
@Transactional
public class ItemAsMapService {

	private static final Logger log = LoggerFactory.getLogger(ItemAsMapService.class);
	
	@Autowired
	private BasicRepository repository;
	
	@Autowired
	private ItemS3Service itemS3Service;
	
	@Autowired
	private RecommendationS3Service recommendationS3Service;
	
	@Autowired @Qualifier("topic")
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * This method return all existing items.
	 * 
	 * @param tenantId is the tenant id.
	 * @param size is number of results that should be returned.
	 * @param from is number of initial results that should be skipped.
	 * @param sortFields is name of fields used for sorting.
	 * @return a <code>List</code> that contains documents or empty <code>List</code> if nothing found.
	 */
	public List<Map<String, Object>> findAll(String tenantId, int size, int from, List<String> sortFields) {
		return repository.findAll("item_" + tenantId.toLowerCase(), "item", size, from, sortFields);
	}
	
	/**
	 * Search for related items based on existing item.
	 * 
	 * @param tenantId is the tenant id.
	 * @param id is the id of item to search for.
	 * @return an instance of <code>ItemRecommendation</code>.
	 */
	public ItemRecommendation similiar(String tenantId, String id) {
		ItemRecommendation itemRecommendation = new ItemRecommendation();
		repository.similiar("item_" + tenantId.toLowerCase(), "item", 1, id).forEach(itemRecommendation::addItem);
		postProcessSimiliar(id, tenantId, itemRecommendation);
		return itemRecommendation;
	}
	
	/**
	 * Search for related item based on certain text.
	 * 
	 * @param tenantId is the tenant id.
	 * @param id is current item id.
	 * @param likeText is the text to search for.
	 * @param fields is the fields to search for.
	 * @return an instance of <code>ItemRecommendation</code>.
	 */
	public ItemRecommendation similiar(String tenantId, String id, String likeText, String... fields) {
		ItemRecommendation itemRecommendation = new ItemRecommendation();
		repository.similiar("item_" + tenantId.toLowerCase(), "item", 1, likeText, fields).forEach(itemRecommendation::addItem);
		postProcessSimiliar(id, tenantId, itemRecommendation);
		return itemRecommendation;
	}
	
	/**
	 * Post processing after searching for similiar items.  This will including putting the file
	 * into S3 storage and sending message notification to queue.
	 * 
	 * @param id is the item id.
	 * @param tenantId is the tenant id.
	 * @param itemRecommendation is the recommendation result.
	 */
	private void postProcessSimiliar(String id, String tenantId, ItemRecommendation itemRecommendation) {
		try {
			recommendationS3Service.putFile(id, tenantId, itemRecommendation);
		} catch (Exception e) {
			log.error("Error pushing recommendation file to S3.", e);
		}
		
		Map<String, Object> addSimiliarItemMessage = new HashMap<>();
		addSimiliarItemMessage.put("id", id);
		addSimiliarItemMessage.put("tenantId", tenantId);
		addSimiliarItemMessage.put("recommendation", itemRecommendation);
		jmsTemplate.send("FISHER.ADD_SIMILIAR_ITEM", new JsonMessageCreator(addSimiliarItemMessage, objectMapper));
	}
	
	/**
	 * Save a new item into database and publish them into S3.
	 * 
	 * @param tenantId is the tenant id.
	 * @param id is the id of the item.
	 * @param mapItem an <code>Item</code> to save.
	 */
	public void save(String tenantId, String id, Map<String, Object> mapItem) {
		repository.saveOrUpdate("item_" + tenantId.toLowerCase(), "item", id, mapItem);
		try {
			itemS3Service.putFile(id, tenantId, mapItem);
		} catch (JsonProcessingException e) {
			log.error("Exception while trying to push item to S3", e);
		}
	}
	
	/**
	 * Delete existing item from database and from S3.
	 * 
	 * @param tenantId is the tenant id.
	 * @param id is the id of the item.
	 * @return <code>true</code> if the item is deleted.
	 */
	public boolean delete(String tenantId, String id) {
		boolean status = repository.delete("item_" + tenantId.toLowerCase(), "item", id); 
		if (status) {
			itemS3Service.deleteFile(id, tenantId);
			recommendationS3Service.deleteFile(id, tenantId);
			
			Map<String,String> jsonMessage = new HashMap<>();
			jsonMessage.put("tenantId", tenantId);
			jsonMessage.put("id", id);
			jmsTemplate.send("FISHER.DELETE_ITEM", new JsonMessageCreator(jsonMessage, objectMapper));
		}
		return status;
	}
	
	/**
	 * Save a bulk of item into database and publish them into S3.
	 * 
	 * @param tenantId is the tenant id.
	 * @param mapItems is the collection of <code>Item</code> to save.
	 */
	public void saveBatch(String tenantId, Map<String, Map<String, Object>> mapItems) {
		repository.saveBatch("item_" + tenantId.toLowerCase(), "item", mapItems);
		mapItems.forEach((id,item) -> {
			try {
				itemS3Service.putFile(id, tenantId, item);
			} catch (JsonProcessingException e) {
				log.error("Exception while trying to push item to S3", e);
			}
		});
	}
	
	/**
	 * Find for certain item based on id.
	 * 
	 * @param tenantId is the tenand id.
	 * @param id is the id to search.
	 * @return the item that matches this id.
	 */
	public Map<String, Object> find(String tenantId, String id) {
		return repository.find("item_" + tenantId.toLowerCase(), "item", id);
	}
	
	/**
	 * Calculate the number of items stored by tenant.
	 * 
	 * @param tenantId is the tenand id.
	 * @return number of items stored by this tenant.
	 */
	public long count(String tenantId) {
		Asserts.notNull(tenantId, "Tenant id can't be null.");
		return repository.count("item_" + tenantId.toLowerCase(), "item");
	}
	
}
