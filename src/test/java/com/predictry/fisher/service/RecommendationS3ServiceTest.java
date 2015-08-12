package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.item.ItemRecommendation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class RecommendationS3ServiceTest {

	@Autowired
	private RecommendationS3Service recommendationS3Service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void putFile() throws IOException {
		ItemRecommendation itemRecommendation = new ItemRecommendation();
		itemRecommendation.setAlgo("similiar");
		itemRecommendation.addItem("item1");
		itemRecommendation.addItem("item2");
		itemRecommendation.addItem("item3");
		recommendationS3Service.putFile("item_x", "CLIENTDEV", itemRecommendation);
		
		// Check the result
		String result = recommendationS3Service.readFile("item_x", "CLIENTDEV");
		@SuppressWarnings("unchecked")
		Map<String,Object> parsedResult = objectMapper.readValue(result, Map.class);
		assertEquals("similiar", parsedResult.get("algo"));
		@SuppressWarnings("unchecked")
		List<String> recommendations = (List<String>) parsedResult.get("items");
		assertEquals(3, recommendations.size());
		assertEquals("item1", recommendations.get(0));
		assertEquals("item2", recommendations.get(1));
		assertEquals("item3", recommendations.get(2));
		
		// Remove the file from S3 (cleaning)
		recommendationS3Service.deleteFile("item_x", "CLIENTDEV");
	}

}
