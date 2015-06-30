package com.predictry.fisher.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.item.Item;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class ItemServiceTest {

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private ElasticsearchTemplate template;
	
	@Before
	public void clean() {
		if (template.indexExists("item_bukalapak")) {
			template.deleteIndex("item_bukalapak");
		}
	}
	
	@Test
	public void createItemIndexIfDoesNotExist() {
		Item item = new Item("id1", "item1", "http://item.url", "http://image.url", "category1");
		item.setTenantId("BUKALAPAK");
		itemService.save(item);
		
		// Check if index is created
		assertTrue(template.indexExists("item_bukalapak"));
		assertTrue(template.typeExists("item_bukalapak", "item"));
		
		// Check if row is created
		template.refresh("item_bukalapak", true);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("item_bukalapak")
			.withTypes("item")
			.withIds(Arrays.asList("id1"))
			.build();
		List<Item> searchResult = template.queryForList(searchQuery, Item.class);
		assertEquals(1, searchResult.size());
		Item savedItem = searchResult.get(0);
		assertEquals("id1", savedItem.getId());
		assertEquals("item1", savedItem.getName());
		assertEquals("http://item.url", savedItem.getItemUrl());
		assertEquals("http://image.url", savedItem.getImageUrl());
		assertEquals("category1", savedItem.getCategory());
	}
	
	@Test
	public void updateExisting() {
		// Create new item
		Item item = new Item("id1", "item1", "http://item.url", "http://image.url", "category1");
		item.setTenantId("BUKALAPAK");
		itemService.save(item);
		
		// Update this stat
		item.setName("item1 updated");
		itemService.save(item);
		
		// Check if it is updated
		template.refresh("item_bukalapak", true);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("item_bukalapak")
			.withTypes("item")
			.withIds(Arrays.asList("id1"))
			.build();
		List<Item> searchResult = template.queryForList(searchQuery, Item.class);
		assertEquals(1, searchResult.size());
		Item savedItem = searchResult.get(0);
		assertEquals("id1", savedItem.getId());
		assertEquals("item1 updated", savedItem.getName());
		assertEquals("http://item.url", savedItem.getItemUrl());
		assertEquals("http://image.url", savedItem.getImageUrl());
		assertEquals("category1", savedItem.getCategory());
	}
	
	@Test
	public void find() {
		Item item = new Item("id1", "item1", "http://item.url", "http://image.url", "category1");
		item.setTenantId("BUKALAPAK");
		itemService.save(item);
		template.refresh("item_bukalapak", true);
		
		Item result = itemService.find("BUKALAPAK", "id1");
		assertNotNull(result);
		assertEquals("id1", result.getId());
		assertEquals("item1", result.getName());
		assertEquals("http://item.url", result.getItemUrl());
		assertEquals("http://image.url", result.getImageUrl());
		assertEquals("category1", result.getCategory());
		
		result = itemService.find("bukalapak", "id1");
		assertNotNull(result);
		assertEquals("id1", result.getId());
		assertEquals("item1", result.getName());
		assertEquals("http://item.url", result.getItemUrl());
		assertEquals("http://image.url", result.getImageUrl());
		assertEquals("category1", result.getCategory());
	}

}
