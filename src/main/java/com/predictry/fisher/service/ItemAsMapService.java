package com.predictry.fisher.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.predictry.fisher.domain.item.ItemRecommendation;
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
		try {
			recommendationS3Service.putFile(id, tenantId, itemRecommendation);
		} catch (Exception e) {
			log.error("Error pushing recommendation file to S3.", e);
		}
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
		try {
			recommendationS3Service.putFile(id, tenantId, itemRecommendation);
		} catch (Exception e) {
			log.error("Error pushing recommendation file to S3.", e);
		}
		return itemRecommendation;
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
