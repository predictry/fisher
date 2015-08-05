package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
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
public class ItemServiceTest {

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
