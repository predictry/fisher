package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
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
import com.predictry.fisher.domain.aggregation.SalesAggregation;
import com.predictry.fisher.domain.aggregation.ViewsAggregation;
import com.predictry.fisher.domain.item.TopScore;
import com.predictry.fisher.domain.item.TopScoreType;
import com.predictry.fisher.domain.pull.PullTime;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.Value;
import com.predictry.fisher.repository.BasicRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class PullServiceTest {

	@Autowired
	private PullService pullService;
	
	@Autowired
	private BasicRepository basicRepository;
	
	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private ElasticsearchTemplate template;
	
	@Before
	public void clean() {
		if (template.indexExists("stat_2015")) {
			template.deleteIndex("stat_2015");
		}
		if (template.indexExists("stat_2014")) {
			template.deleteIndex("stat_2014");
		}
	}
	
	@Test
	public void aggregateFile() throws IOException {
		// Stat for 'tenant1'
		File file = new File(getClass().getResource("/sample_tenant1.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		Stat statTenant1 = pullService.aggregate(sources, "tenant1", LocalDateTime.parse("2015-06-19T03:00:00"));
		assertEquals(2l, statTenant1.getViews().getOverall().longValue());
		assertEquals(1000001.1, statTenant1.getSales().getOverall().doubleValue(), 0.5);
		assertEquals(28l, statTenant1.getItemPurchased().getOverall().longValue());
		assertEquals(2l, statTenant1.getUniqueVisitor().getOverall().longValue());
		assertEquals(4l, statTenant1.getOrders().getOverall().longValue());
		assertEquals(2l, statTenant1.getUniqueItemPurchased().getOverall().longValue());
		
		// Stat for 'tenant2'
		file = new File(getClass().getResource("/sample_tenant2.log").getFile());
		sources = Files.readAllLines(file.toPath());
		Stat statTenant2 = pullService.aggregate(sources, "tenant2", LocalDateTime.parse("2015-06-19T03:00:00"));
		assertEquals(3l, statTenant2.getViews().getOverall().longValue());
		assertEquals(18000.0, statTenant2.getSales().getOverall().doubleValue(), 0.5);
		assertEquals(27l, statTenant2.getItemPurchased().getOverall().longValue());
		assertEquals(3l, statTenant2.getUniqueVisitor().getOverall().longValue());
		assertEquals(3l, statTenant2.getOrders().getOverall().longValue());
		assertEquals(2l, statTenant2.getUniqueItemPurchased().getOverall().longValue());
		
		// Check if items were created
		template.refresh("item_tenant1", true);
		assertTrue(template.indexExists("item_tenant1"));
		assertTrue(template.typeExists("item_tenant1", "item"));
		assertEquals(7, basicRepository.count("item_tenant1", "item"));
		
		Map<String, Object> result = basicRepository.find("item_tenant1", "item", "item01");
		assertEquals("The Item 01", result.get("name"));
		assertEquals("https://www.xxx_item01.com", result.get("item_url"));
		assertEquals("https://www.yyy_item01.com", result.get("img_url"));
		assertEquals("Cat01", result.get("category"));
		
		result = basicRepository.find("item_tenant1", "item", "item03");
		assertEquals("The Item 02", result.get("name"));
		assertEquals("https://www.xxx_item02.com", result.get("item_url"));
		assertEquals("https://www.yyy_item02.com", result.get("img_url"));
		assertEquals("Sepeda", result.get("category"));
		
		result = basicRepository.find("item_tenant1", "item", "item04");
		assertEquals("The Item 03", result.get("name"));
		assertEquals("https://www.xxx_item03.com", result.get("item_url"));
		assertEquals("https://www.yyy_item03.com", result.get("img_url"));
		assertEquals("Fashion", result.get("category"));
	}

	public void email() throws IOException {
		File file = new File(getClass().getResource("/sample_email.log").getFile());
        List<String> sources = Files.readAllLines(file.toPath());
        pullService.aggregate(sources, "BANYAKDEALCOM", LocalDateTime.parse("2016-02-19T03:00:00"));

	}
	
	@Test
	public void aggregateBuy() throws IOException {
		File file = new File(getClass().getResource("/sample_buy.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		
		Stat statTenant1 = pullService.aggregate(sources, "tenant1", LocalDateTime.parse("2015-06-19T03:00:00"));
		assertEquals(2l, statTenant1.getOrders().getOverall().longValue());
		assertEquals(21l, statTenant1.getItemPurchased().getOverall().longValue());
		assertEquals(5l, statTenant1.getUniqueItemPurchased().getOverall().longValue());
	}
	
	@Test
	public void aggregateRecommendation() throws IOException {
		File file = new File(getClass().getResource("/sample_recommendation.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		
		Stat statTenant1 = pullService.aggregate(sources, "tenant1", LocalDateTime.parse("2015-06-19T03:00:00"));
		assertEquals(6, statTenant1.getViews().getOverall().intValue());
		assertEquals(4, statTenant1.getViews().getRecommended().intValue());
		assertEquals(0, statTenant1.getViews().getRegular().intValue());
		
		assertEquals(0, statTenant1.getCartBoost().longValue());
	}
	
	@Test
	public void aggregateBuyRecommendation() throws IOException {
		File file = new File(getClass().getResource("/sample_buy_recommendation.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		
		Stat statTenant1 = pullService.aggregate(sources, "tenant1", LocalDateTime.parse("2015-06-19T03:00:00"));
		Value uniqueItemPurchased = statTenant1.getUniqueItemPurchased();
		assertEquals(5, uniqueItemPurchased.getOverall().longValue());
		assertEquals(3, uniqueItemPurchased.getRecommended().longValue());
		assertEquals(0, uniqueItemPurchased.getRegular().longValue());
		
		assertEquals(0.75, statTenant1.getCartBoost(), 0.001);
	}
	
	@Test
	public void topScoreFile() throws IOException {
		File file = new File(getClass().getResource("/sample.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		ViewsAggregation viewsAggregation = new ViewsAggregation();
		SalesAggregation salesAggregation = new SalesAggregation();
		pullService.aggregate(sources, "tenant1", LocalDateTime.parse("2015-06-19T03:00:00"), viewsAggregation, salesAggregation);
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
	}
	
	@Test(expected=RuntimeException.class)
	public void aggregateFileWithInvalidTimeMetadata() throws IOException {
		File file = new File(getClass().getResource("/sample_invalid_metadata.log").getFile());
		List<String> sources = Files.readAllLines(file.toPath());
		pullService.aggregate(sources, "tenant1", LocalDateTime.parse("2015-06-19T03:00:00"));		
	}
	
}
