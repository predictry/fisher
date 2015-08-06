package com.predictry.fisher.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.config.TestRootConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class ItemS3ServiceTest {

	@Autowired
	private ItemS3Service itemS3Service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void putFile() throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("name", "sample_item");
		data.put("price", "18");
		data.put("img_url", "http://xxx/image");
		itemS3Service.putFile("12345", "CLIENTDEV", data);
		
		// Check the result
		String result = itemS3Service.readFile("12345", "CLIENTDEV");
		@SuppressWarnings("unchecked")
		Map<String,String> parsedResult = objectMapper.readValue(result, Map.class);
		assertEquals("sample_item", parsedResult.get("name"));
		assertEquals("18", parsedResult.get("price"));
		assertEquals("http://xxx/image", parsedResult.get("img_url"));
		
		// Remove the item file from S3 (cleaning)
		itemS3Service.deleteFile("12345", "CLIENTDEV");
	}
	
}
