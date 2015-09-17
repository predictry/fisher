package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.predictry.fisher.config.TestRootConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class ItemAsMapServiceTest {

	@Autowired
	private ItemAsMapService itemAsMapService;
	
	@Autowired
	private ElasticsearchTemplate template;
	
	@Before
	public void clean() {
		if (template.indexExists("item_bukalapak")) {
			template.deleteIndex("item_bukalapak");
		}
	}
	
	@Test
	public void findAll() {
		// Saving items
		for (int i=1; i<=15; i++) {
			Map<String, Object> item = new HashMap<>();
			item.put("name", "item" + String.format("%02d", i));
			item.put("item_url", "http://item.url");
			item.put("img_url", "http://image.url");
			item.put("category", "category1");
			itemAsMapService.save("BUKALAPAK", "id" + i, item);
		}
		
		List<String> sortFields = new ArrayList<>();
		sortFields.add("name");
		
		// Try to search for all of them
		List<Map<String,Object>> results = itemAsMapService.findAll("BUKALAPAK", 15, 0, sortFields);
		assertEquals(15, results.size());
		for (int i=1; i<=15; i++) {
			Map<String, Object> item = results.get(i-1);
			assertEquals("id" + i, item.get("id"));
			assertEquals("item" + String.format("%02d", i), item.get("name"));
			assertEquals("http://item.url", item.get("item_url"));
			assertEquals("http://image.url", item.get("img_url"));
			assertEquals("category1", item.get("category"));
		}
		
		// With a pagination with 5 items per page, select only the first page.
		results = itemAsMapService.findAll("BUKALAPAK", 5, 0, sortFields);
		assertEquals(5, results.size());
		for (int i=1; i<=5; i++) {
			Map<String, Object> item = results.get(i-1);
			assertEquals("id" + i, item.get("id"));
			assertEquals("item" + String.format("%02d", i), item.get("name"));
			assertEquals("http://item.url", item.get("item_url"));
			assertEquals("http://image.url", item.get("img_url"));
			assertEquals("category1", item.get("category"));
		}
		
		// With a pagination with 5 items per page, select only the second page.
		results = itemAsMapService.findAll("BUKALAPAK", 5, 5, sortFields);
		assertEquals(5, results.size());
		for (int i=1; i<=5; i++) {
			Map<String, Object> item = results.get(i-1);
			assertEquals("id" + (5 + i), item.get("id"));
			assertEquals("item" + String.format("%02d", (5 + i)), item.get("name"));
			assertEquals("http://item.url", item.get("item_url"));
			assertEquals("http://image.url", item.get("img_url"));
			assertEquals("category1", item.get("category"));
		}
		
		// With a pagination with 6 items per page, select only the third page.
		results = itemAsMapService.findAll("BUKALAPAK", 5, 10, sortFields);
		assertEquals(5, results.size());
		for (int i=1; i<=5; i++) {
			Map<String, Object> item = results.get(i-1);
			assertEquals("id" + (10 + i), item.get("id"));
			assertEquals("item" + String.format("%02d", (10 + i)), item.get("name"));
			assertEquals("http://item.url", item.get("item_url"));
			assertEquals("http://image.url", item.get("img_url"));
			assertEquals("category1", item.get("category"));
		}
	}
	
	@Test
	public void createItemIndexIfDoesNotExist() {
		Map<String, Object> item = new HashMap<>();
		item.put("name", "item1");
		item.put("item_url", "http://item.url");
		item.put("img_url", "http://image.url");
		item.put("category", "category1");
		itemAsMapService.save("BUKALAPAK", "id1", item);
		
		// Check if index is created
		assertTrue(template.indexExists("item_bukalapak"));
		assertTrue(template.typeExists("item_bukalapak", "item"));
		
		// Check if row is created
		template.refresh("item_bukalapak", true);
		Map<String, Object> result = itemAsMapService.find("BUKALAPAK", "id1");
		assertEquals("id1", result.get("id"));
		assertEquals("item1", result.get("name"));
		assertEquals("http://item.url", result.get("item_url"));
		assertEquals("http://image.url", result.get("img_url"));
		assertEquals("category1", result.get("category"));
	}
	
	@Test
	public void updateExisting() {
		// Create new item
		Map<String, Object> item = new HashMap<>();
		item.put("name", "item1");
		item.put("item_url", "http://item.url");
		item.put("img_url", "http://image.url");
		item.put("category", "category1");
		itemAsMapService.save("BUKALAPAK", "id1", item);
		
		// Update this stat
		item.put("name", "item1 updated");
		itemAsMapService.save("BUKALAPAK", "id1", item);
		
		// Check if row is created
		template.refresh("item_bukalapak", true);
		Map<String, Object> result = itemAsMapService.find("BUKALAPAK", "id1");
		assertEquals("id1", result.get("id"));
		assertEquals("item1 updated", result.get("name"));
		assertEquals("http://item.url", result.get("item_url"));
		assertEquals("http://image.url", result.get("img_url"));
		assertEquals("category1", result.get("category"));
	}
	
	@Test
	public void find() {
		// Create new item
		Map<String, Object> item = new HashMap<>();
		item.put("name", "item1");
		item.put("item_url", "http://item.url");
		item.put("img_url", "http://image.url");
		item.put("category", "category1");
		itemAsMapService.save("BUKALAPAK", "id1", item);
		template.refresh("item_bukalapak", true);
		
		Map<String, Object> result = itemAsMapService.find("BUKALAPAK", "id1");
		assertEquals("id1", result.get("id"));
		assertEquals("item1", result.get("name"));
		assertEquals("http://item.url", result.get("item_url"));
		assertEquals("http://image.url", result.get("img_url"));
		assertEquals("category1", result.get("category"));
		
		result = itemAsMapService.find("bukalapak", "id1");
		assertEquals("id1", result.get("id"));
		assertEquals("item1", result.get("name"));
		assertEquals("http://item.url", result.get("item_url"));
		assertEquals("http://image.url", result.get("img_url"));
		assertEquals("category1", result.get("category"));
	}
	
	@Test
	public void count() {
		Map<String, Object> item = new HashMap<>();
		item.put("name", "item1");
		item.put("item_url", "http://item.url");
		item.put("img_url", "http://image.url");
		item.put("category", "category1");
		itemAsMapService.save("BUKALAPAK", "id1", item);
		template.refresh("item_bukalapak", true);
		assertEquals(1l, itemAsMapService.count("BUKALAPAK"));
		
		item.put("name", "item2");
		item.put("item_url", "http://item.url");
		item.put("img_url", "http://image.url");
		item.put("category", "category1");
		itemAsMapService.save("BUKALAPAK", "id2", item);
		template.refresh("item_bukalapak", true);
		assertEquals(2l, itemAsMapService.count("BUKALAPAK"));
		
		item.put("name", "item3");
		item.put("item_url", "http://item.url");
		item.put("img_url", "http://image.url");
		item.put("category", "category1");
		itemAsMapService.save("BUKALAPAK", "id3", item);
		template.refresh("item_bukalapak", true);
		assertEquals(3l, itemAsMapService.count("BUKALAPAK"));
	}

}
