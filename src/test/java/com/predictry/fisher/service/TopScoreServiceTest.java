package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.TopScore;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class TopScoreServiceTest {
/*
	@Autowired
	private TopScoreService topScoreService;
	
	@Autowired
	private ElasticsearchTemplate template;
	
	@Before
	public void clearIndex() {
		template.deleteIndex(TopScore.class);
		template.createIndex(TopScore.class);
		template.putMapping(TopScore.class);
		template.refresh(TopScore.class, true);
	}
	
	@Test
	public void testTopHitCreatedIfItDoesNotExists() {
		TopScore<Long> topHits = topScoreService.getTopHits();
		assertNotNull(topHits);
		assertEquals(TopScore.TOP_HIT, topHits.getType());
		assertEquals(0, topHits.getItems().size());
	}
	
	@Test
	public void testTopSalesCreatedIfItDoesNotExists() {
		TopScore<Double> topSales = topScoreService.getTopSales();
		assertNotNull(topSales);
		assertEquals(TopScore.TOP_SALES, topSales.getType());
		assertEquals(0, topSales.getItems().size());
	}
	
	@Test
	public void testAddNewTopHit() {
		TopScore<Long> topHits = topScoreService.getTopHits();
		Item<Long> itemScore1 = new Item<Long>("id1", "product1", "http://xxx", 10l);
		Item<Long> itemScore2 = new Item<Long>("id2", "product2", "http://xxx", 20l);
		Item<Long> itemScore3 = new Item<Long>("id3", "product3", "http://xxx", 30l);
		topHits = topScoreService.addNewTopHits(itemScore1);
		assertNotNull(topHits);
		assertEquals(1, topHits.getItems().size());
		assertEquals("id1", topHits.getItems().get(0).getId());
		
		topHits = topScoreService.addNewTopHits(itemScore2);
		assertNotNull(topHits);
		assertEquals(2, topHits.getItems().size());
		assertEquals("id2", topHits.getItems().get(0).getId());
		assertEquals("id1", topHits.getItems().get(1).getId());
		
		topHits = topScoreService.addNewTopHits(itemScore3);
		assertNotNull(topHits);
		assertEquals(3, topHits.getItems().size());
		assertEquals("id3", topHits.getItems().get(0).getId());
		assertEquals("id2", topHits.getItems().get(1).getId());
		assertEquals("id1", topHits.getItems().get(2).getId());
	}
*/	
}
