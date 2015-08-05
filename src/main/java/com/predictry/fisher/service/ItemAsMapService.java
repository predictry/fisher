package com.predictry.fisher.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.predictry.fisher.repository.BasicRepository;

@Service
@Transactional
public class ItemAsMapService {

	@Autowired
	private BasicRepository repository;
	
	/**
	 * Save a new item into database.
	 * 
	 * @param item an <code>Item</code> to save.
	 */
	public void save(String tenantId, String id, Map<String, Object> mapItem) {
		repository.saveOrUpdate("item_" + tenantId.toLowerCase(), "item", id, mapItem);
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
