package com.predictry.fisher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.util.Helper;
import com.predictry.fisher.service.ItemService;

@RestController
public class ItemController {

	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemService itemService;
	
	/**
	 * Retrieve information about an item based on tenant id and item id.
	 * 
	 * @return JSON value (<code>Item</code>).
	 */
	@RequestMapping("/items/{tenantId}/{itemId}")
	public Item findItem(@PathVariable String tenantId, @PathVariable String itemId) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		log.info("Find item [" + itemId + "] for tenant id [" + tenantId + "]" );
		return itemService.find(tenantId, itemId);
	}
	
	/**
	 * Retrieve number of items stored for a tenant id.
	 * 
	 * @return number of items.
	 */
	@RequestMapping("/items/{tenantId}/count")
	public Long count(@PathVariable String tenantId) {
		tenantId = Helper.tenantIdRemapping(tenantId);
		log.info("Calculating number of items for tenant id [" + tenantId + "]");
		return itemService.count(tenantId);
	}
}
