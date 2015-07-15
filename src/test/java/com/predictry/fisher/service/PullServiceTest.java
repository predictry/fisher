package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.predictry.fisher.domain.aggregation.SalesAggregation;
import com.predictry.fisher.domain.aggregation.ViewsAggregation;
import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.TopScore;
import com.predictry.fisher.domain.item.TopScoreType;
import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.domain.stat.Stat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class PullServiceTest {

	@Autowired
	private PullService pullService;
	
	@Autowired
	private ElasticsearchTemplate template;
	
	@Test
	public void aggregateFile() throws IOException {
		File file = new File(getClass().getResource("/sample.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		Map<String, Stat> stats = pullService.aggregate(sources, LocalDateTime.parse("2015-06-19T03:00:00"));
		
		// Stat for 'tenant1'
		Stat statTenant1 = stats.get("tenant1");
		assertEquals(2l, statTenant1.getViews().getOverall().longValue());
		assertEquals(1000001.1, statTenant1.getSales().getOverall().doubleValue(), 0.5);
		assertEquals(28l, statTenant1.getItemPurchased().getOverall().longValue());
		assertEquals(2l, statTenant1.getUniqueVisitor().getOverall().longValue());
		assertEquals(4l, statTenant1.getOrders().getOverall().longValue());
		
		// Stat for 'tenant2'
		Stat statTenant2 = stats.get("tenant2");
		assertEquals(3l, statTenant2.getViews().getOverall().longValue());
		assertEquals(18000.0, statTenant2.getSales().getOverall().doubleValue(), 0.5);
		assertEquals(27l, statTenant2.getItemPurchased().getOverall().longValue());
		assertEquals(3l, statTenant2.getUniqueVisitor().getOverall().longValue());
		assertEquals(3l, statTenant2.getOrders().getOverall().longValue());
		
		// Check if items were created
		assertTrue(template.indexExists("item_tenant1"));
		assertTrue(template.typeExists("item_tenant1", "item"));
		template.refresh("item_tenant1", true);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("item_tenant1")
			.withTypes("item")
			.withIds(Arrays.asList("item01", "item02", "item03", "item04", "item05", "item06", "item07", "item08", "item09", "item10"))
			.build();
		List<Item> searchResult = template.queryForList(searchQuery, Item.class);
		assertEquals(7, searchResult.size());
		
		Item item1 = searchResult.stream().filter(i -> i.getId().equals("item01")).findFirst().get();
		assertEquals("The Item 01", item1.getName());
		assertEquals("https://www.xxx_item01.com", item1.getItemUrl());
		assertEquals("https://www.yyy_item01.com", item1.getImageUrl());
		assertEquals("Cat01", item1.getCategory());
		
		Item item3 = searchResult.stream().filter(i -> i.getId().equals("item03")).findFirst().get();
		assertEquals("The Item 02", item3.getName());
		assertEquals("https://www.xxx_item02.com", item3.getItemUrl());
		assertEquals("https://www.yyy_item02.com", item3.getImageUrl());
		assertEquals("Sepeda", item3.getCategory());
		
		Item item4 = searchResult.stream().filter(i -> i.getId().equals("item04")).findFirst().get();
		assertEquals("The Item 03", item4.getName());
		assertEquals("https://www.xxx_item03.com", item4.getItemUrl());
		assertEquals("https://www.yyy_item03.com", item4.getImageUrl());
		assertEquals("Fashion", item4.getCategory());
	}
	
	@Test
	public void aggregateBuy() throws IOException {
		File file = new File(getClass().getResource("/sample_buy.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		Map<String, Stat> stats = pullService.aggregate(sources, LocalDateTime.parse("2015-06-19T03:00:00"));
		
		Stat statTenant1 = stats.get("tenant1");
		assertEquals(2l, statTenant1.getOrders().getOverall().longValue());
		assertEquals(21l, statTenant1.getItemPurchased().getOverall().longValue());
	}
	
	@Test
	public void topScoreFile() throws IOException {
		File file = new File(getClass().getResource("/sample.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		ViewsAggregation viewsAggregation = new ViewsAggregation();
		SalesAggregation salesAggregation = new SalesAggregation();
		pullService.aggregate(sources, LocalDateTime.parse("2015-06-19T03:00:00"), viewsAggregation, salesAggregation);
		List<TopScore> topScores = pullService.topScore(viewsAggregation, salesAggregation, LocalDateTime.parse("2015-06-19T03:00:00"));
		assertEquals(4, topScores.size());
		
		TopScore tenant1Views = topScores.stream().filter(t -> (t.getType() == TopScoreType.HIT && t.getTenantId().equals("tenant1"))).findFirst().get();
		assertEquals(2, tenant1Views.getItems().size());
		assertEquals("item01", tenant1Views.getItemAtRank(1).getId());
		assertEquals(1.0, tenant1Views.getItemAtRank(1).getScore(), 0.1);
		assertEquals("item02", tenant1Views.getItemAtRank(2).getId());
		assertEquals(1.0, tenant1Views.getItemAtRank(2).getScore(), 0.1);
		
		TopScore tenant2Views = topScores.stream().filter(t -> (t.getType() == TopScoreType.HIT && t.getTenantId().equals("tenant2"))).findFirst().get();
		assertEquals(2, tenant2Views.getItems().size());
		assertEquals("item10", tenant2Views.getItemAtRank(1).getId());
		assertEquals(2.0, tenant2Views.getItemAtRank(1).getScore(), 0.1);
		assertEquals("item07", tenant2Views.getItemAtRank(2).getId());
		assertEquals(1.0, tenant2Views.getItemAtRank(2).getScore(), 0.1);

		TopScore tenant1Sales = topScores.stream().filter(t -> (t.getType() == TopScoreType.SALES && t.getTenantId().equals("tenant1"))).findFirst().get();
		assertEquals(2, tenant1Sales.getItems().size());
		assertEquals("item01", tenant1Sales.getItemAtRank(1).getId());
		assertEquals(15.0, tenant1Sales.getItemAtRank(1).getScore(), 0.1);
		assertEquals("item03", tenant1Sales.getItemAtRank(2).getId());
		assertEquals(13.0, tenant1Sales.getItemAtRank(2).getScore(), 0.1);
		
		TopScore tenant2Sales = topScores.stream().filter(t -> (t.getType() == TopScoreType.SALES && t.getTenantId().equals("tenant2"))).findFirst().get();
		assertEquals(2, tenant2Sales.getItems().size());
		assertEquals("item05", tenant2Sales.getItemAtRank(1).getId());
		assertEquals(19.0, tenant2Sales.getItemAtRank(1).getScore(), 0.1);
		assertEquals("item03", tenant2Sales.getItemAtRank(2).getId());
		assertEquals(8.0, tenant2Sales.getItemAtRank(2).getScore(), 0.1);
	}

	@Test
	public void update() {
		PullTime pullTime = pullService.getDefaultPullTime();
		assertNotNull(pullTime);
		pullTime.setForTime(LocalDateTime.parse("2007-12-03T10:15:30"));
		PullTime updatedPullTime = pullService.update(pullTime);
		assertEquals(pullTime.getId(), updatedPullTime.getId());
		assertEquals("2007-12-03T10:15:30", updatedPullTime.getForTime().toString());
		assertNull(updatedPullTime.getLastExecutedTime());
		assertEquals(0, updatedPullTime.getRepeat().intValue());
	}
	
	@Test(expected=RuntimeException.class)
	public void aggregateFileWithInvalidTimeMetadata() throws IOException {
		File file = new File(getClass().getResource("/sample_invalid_metadata.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		pullService.aggregate(sources, LocalDateTime.parse("2015-06-19T03:00:00"));		
	}
	
}
