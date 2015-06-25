package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.predictry.fisher.domain.item.TopScore;
import com.predictry.fisher.domain.item.TopScore.TYPE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class TopScoreServiceTest {

	@Autowired
	private TopScoreService topScoreService;
	
	@Autowired
	private ElasticsearchTemplate template;

	@Before
	public void clean() {
		if (template.indexExists("top_2015")) {
			template.deleteIndex("top_2015");
		}
		if (template.indexExists("top_2014")) {
			template.deleteIndex("top_2014");
		}
	}
	
	@Test
	public void createLogIndexIfDoesNotExist() {
		TopScore topScore = new TopScore(TYPE.VIEWS);
		topScore.addNewScore("itemB", "Product B", "http://www.xxx.com", 28.0);
		topScore.addNewScore("itemD", "Product D", "http://www.yyy.com", 20.0);
		topScore.addNewScore("itemA", "Product A", "http://www.xxx.com", 10.0);
		topScore.addNewScore("itemC", "Product C", "http://www.yyy.com", 2.0);
		topScore.setTenantId("BUKALAPAK");
		topScore.setTime(LocalDateTime.parse("2015-02-01T10:00:00"));
		topScoreService.save(topScore);
		
		// Check if index is created
		assertTrue(template.indexExists("top_2015"));
		assertTrue(template.typeExists("top_2015", "BUKALAPAK"));
		
		// Check if document is created
		template.refresh("top_2015", true);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("top_2015")
			.withTypes("BUKALAPAK")
			.withIds(Arrays.asList("2015-02-01T10:00"))
			.build();
		List<TopScore> searchResult = template.queryForList(searchQuery, TopScore.class);
		assertEquals(1, searchResult.size());
		TopScore savedStat = searchResult.get(0);
		assertEquals("2015-02-01T10:00:00", savedStat.getTime().format(DateTimeFormatter.ISO_DATE_TIME));
		assertEquals(TYPE.VIEWS, savedStat.getType());
		assertEquals(4, savedStat.getItems().size());
		assertEquals("itemB", savedStat.getItemAtRank(1).getId());
		assertEquals(28.0, savedStat.getItemAtRank(1).getScore(), 0.1);
		assertEquals("itemD", savedStat.getItemAtRank(2).getId());
		assertEquals(20.0, savedStat.getItemAtRank(2).getScore(), 0.1);
		assertEquals("itemA", savedStat.getItemAtRank(3).getId());
		assertEquals(10.0, savedStat.getItemAtRank(3).getScore(), 0.1);
		assertEquals("itemC", savedStat.getItemAtRank(4).getId());
		assertEquals(2.0, savedStat.getItemAtRank(4).getScore(), 0.1);
	}
	
	@Test
	public void updateExistingTopScore() {
		TopScore topScore = new TopScore(TYPE.VIEWS);
		topScore.addNewScore("itemB", "Product B", "http://www.xxx.com", 28.0);
		topScore.addNewScore("itemD", "Product D", "http://www.yyy.com", 20.0);
		topScore.addNewScore("itemA", "Product A", "http://www.xxx.com", 10.0);
		topScore.addNewScore("itemC", "Product C", "http://www.yyy.com", 2.0);
		topScore.setTenantId("BUKALAPAK");
		topScore.setTime(LocalDateTime.parse("2015-02-01T10:00:00"));
		topScoreService.save(topScore);
		
		// Update
		topScore.addNewScore("itemE", "Product E", "http://www.eee.com", 30.0);
		topScoreService.save(topScore);
		
		// Check if top score is updated
		template.refresh("top_2015", true);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("top_2015")
			.withTypes("BUKALAPAK")
			.withIds(Arrays.asList("2015-02-01T10:00"))
			.build();
		List<TopScore> searchResult = template.queryForList(searchQuery, TopScore.class);
		assertEquals(1, searchResult.size());
		TopScore savedStat = searchResult.get(0);
		assertEquals("2015-02-01T10:00:00", savedStat.getTime().format(DateTimeFormatter.ISO_DATE_TIME));
		assertEquals(TYPE.VIEWS, savedStat.getType());
		assertEquals(5, savedStat.getItems().size());
		assertEquals("itemE", savedStat.getItemAtRank(1).getId());
		assertEquals(30.0, savedStat.getItemAtRank(1).getScore(), 0.1);
		assertEquals("itemB", savedStat.getItemAtRank(2).getId());
		assertEquals(28.0, savedStat.getItemAtRank(2).getScore(), 0.1);
		assertEquals("itemD", savedStat.getItemAtRank(3).getId());
		assertEquals(20.0, savedStat.getItemAtRank(3).getScore(), 0.1);
		assertEquals("itemA", savedStat.getItemAtRank(4).getId());
		assertEquals(10.0, savedStat.getItemAtRank(4).getScore(), 0.1);
		assertEquals("itemC", savedStat.getItemAtRank(5).getId());
		assertEquals(2.0, savedStat.getItemAtRank(5).getScore(), 0.1);
	}

}
