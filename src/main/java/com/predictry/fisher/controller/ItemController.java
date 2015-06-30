package com.predictry.fisher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.ErrorMessage;
import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.service.ItemService;

@RestController
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	/**
	 * Retrieve information about an item based on tenant id and item id.
	 * 
	 * @return JSON value (<code>Item</code>).
	 */
	@RequestMapping("/items/{tenantId}/{itemId}")
	public Item findItem(@PathVariable String tenantId, @PathVariable String itemId) {
		return itemService.find(tenantId, itemId);
	}
	
	/**
	 * General error handler for this controller.
	 */
	@ExceptionHandler(value={Exception.class, RuntimeException.class})
	public ErrorMessage error(Exception ex) {
		ErrorMessage error = new ErrorMessage(ex.getMessage());
		return error;
	}
	
}
