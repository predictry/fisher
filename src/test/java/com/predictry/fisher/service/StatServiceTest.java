package com.predictry.fisher.service;

import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.overview.StatOverview;
import com.predictry.fisher.domain.stat.Stat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader=AnnotationConfigContextLoader.class)
public class StatServiceTest {

	@Autowired
	private StatService statService;
	
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
	public void createLogIndexIfDoesNotExist() {
		Stat stat = new Stat("2015-12-15T10:00:00", 10l, 1000.0, 5l, 20l, 10l, 5l);
		statService.save(stat, "BUKALAPAK");
		
		// Check if index is created
		assertTrue(template.indexExists("stat_2015"));
		assertTrue(template.typeExists("stat_2015", "BUKALAPAK"));
		
		// Check if row is created
		template.refresh("stat_2015", true);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("stat_2015")
			.withTypes("BUKALAPAK")
			.withIds(Arrays.asList("2015-12-15T10:00:00"))
			.build();
		List<Stat> searchResult = template.queryForList(searchQuery, Stat.class);
		assertEquals(1, searchResult.size());
		Stat savedStat = searchResult.get(0);
		assertEquals("2015-12-15T10:00:00", savedStat.getTime());
		assertEquals(10l, savedStat.getViews().longValue());
		assertEquals(1000.0, savedStat.getSales(), 0.1);
		assertEquals(5l, savedStat.getItemPerCart().longValue());
		assertEquals(20l, savedStat.getItemPurchased().longValue());
		assertEquals(10l, savedStat.getOrders().longValue());
		assertEquals(5l, savedStat.getUniqueVisitor().longValue());
	}
	
	@Test
	public void updateExisting() {
		// Create new stat
		Stat stat = new Stat("2015-12-15T10:00:00", 10l, 1000.0, 5l, 20l, 10l, 5l);
		statService.save(stat, "BUKALAPAK");
		
		// Update this stat
		stat.addViews(200l);
		statService.save(stat, "BUKALAPAK");
		
		// Check if it is updated
		template.refresh("stat_2015", true);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("stat_2015")
			.withTypes("BUKALAPAK")
			.withIds(Arrays.asList("2015-12-15T10:00:00"))
			.build();
		List<Stat> searchResult = template.queryForList(searchQuery, Stat.class);
		assertEquals(1, searchResult.size());
		Stat savedStat = searchResult.get(0);
		assertEquals("2015-12-15T10:00:00", savedStat.getTime());
		assertEquals(210l, savedStat.getViews().longValue());
		assertEquals(1000.0, savedStat.getSales(), 0.1);
		assertEquals(5l, savedStat.getItemPerCart().longValue());
		assertEquals(20l, savedStat.getItemPurchased().longValue());
		assertEquals(10l, savedStat.getOrders().longValue());
		assertEquals(5l, savedStat.getUniqueVisitor().longValue());
	}
	
	@Test
	public void statOverview() {
		// Create dummy data
		template.createIndex("stat_2015");
		template.createIndex("stat_2014");
		template.refresh("stat_2015", true);
		template.refresh("stat_2014", true);
		
		IndexQuery idxStat1 = new IndexQuery();
		Stat stat1 = new Stat("2014-01-01T01:00:00", 100l, 10.0, 5l, 20l, 1l, 90l);
		idxStat1.setIndexName("stat_2014");
		idxStat1.setType("BUKALAPAK");
		idxStat1.setId(stat1.getTime());
		idxStat1.setObject(stat1);
		
		IndexQuery idxStat2 = new IndexQuery();
		Stat stat2 = new Stat("2014-01-01T02:00:00", 200l, 5.0, 3l, 10l, 2l, 80l);
		idxStat2.setIndexName("stat_2014");
		idxStat2.setType("BUKALAPAK");
		idxStat2.setId(stat2.getTime());
		idxStat2.setObject(stat2);
		
		IndexQuery idxStat3 = new IndexQuery();
		Stat stat3 = new Stat("2015-02-01T10:00:00", 50l, 3.0, 2l, 15l, 3l, 70l);
		idxStat3.setIndexName("stat_2015");
		idxStat3.setType("BUKALAPAK");
		idxStat3.setId(stat3.getTime());
		idxStat3.setObject(stat3);
		
		IndexQuery idxStat4 = new IndexQuery();
		Stat stat4 = new Stat("2015-03-01T11:00:00", 30l, 2.0, 1l, 13l, 2l, 60l);
		idxStat4.setIndexName("stat_2015");
		idxStat4.setType("BUKALAPAK");
		idxStat4.setId(stat4.getTime());
		idxStat4.setObject(stat4);
		
		IndexQuery idxStat5 = new IndexQuery();
		Stat stat5 = new Stat("2014-01-01T01:00:00", 10l, 1.0, 3l, 2l, 1l, 9l);
		idxStat5.setIndexName("stat_2014");
		idxStat5.setType("SUPERBUY");
		idxStat5.setId(stat5.getTime());
		idxStat5.setObject(stat5);
		
		IndexQuery idxStat6 = new IndexQuery();
		Stat stat6 = new Stat("2014-01-01T02:00:00", 20l, 3.0, 1l, 1l, 2l, 8l);
		idxStat6.setIndexName("stat_2014");
		idxStat6.setType("SUPERBUY");
		idxStat6.setId(stat6.getTime());
		idxStat6.setObject(stat6);
		
		IndexQuery idxStat7 = new IndexQuery();
		Stat stat7 = new Stat("2015-02-01T10:00:00", 10l, 1.0, 2l, 1l, 3l, 7l);
		idxStat7.setIndexName("stat_2015");
		idxStat7.setType("SUPERBUY");
		idxStat7.setId(stat7.getTime());
		idxStat7.setObject(stat7);

		template.index(idxStat1);
		template.index(idxStat2);
		template.index(idxStat3);
		template.index(idxStat4);
		template.index(idxStat5);
		template.index(idxStat6);
		template.index(idxStat7);
	
        template.refresh("stat_2014", true);
        template.refresh("stat_2015", true);
        
        // Stat for Bukalapak at 2014
        StatOverview statOverview = statService.overview(LocalDateTime.parse("2014-01-01T01:00:00"), LocalDateTime.parse("2014-12-31T01:00:00"), "BUKALAPAK");
        assertEquals(300l, statOverview.getPageView().getOverall().longValue());
        assertEquals(15.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(4l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(30l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().longValue());
        assertEquals(170l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Bukalapak at 2015 month 2
        statOverview = statService.overview(LocalDateTime.parse("2015-02-01T01:00:00"), LocalDateTime.parse("2015-02-10T01:00:00"), "BUKALAPAK");
        assertEquals(50l, statOverview.getPageView().getOverall().longValue());
        assertEquals(3.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(2l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(15l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().longValue());
        assertEquals(70l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Bukalapak at 2014-2015
        statOverview = statService.overview(LocalDateTime.parse("2014-01-01T01:00:00"), LocalDateTime.parse("2015-12-31T01:00:00"), "BUKALAPAK");
        assertEquals(380l, statOverview.getPageView().getOverall().longValue());
        assertEquals(20.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(2l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(58l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(8l, statOverview.getOrders().longValue());
        assertEquals(300l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Superbuy at 2014
        statOverview = statService.overview(LocalDateTime.parse("2014-01-01T01:00:00"), LocalDateTime.parse("2014-12-31T01:00:00"), "SUPERBUY");
        assertEquals(30l, statOverview.getPageView().getOverall().longValue());
        assertEquals(4.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(2l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(3l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().longValue());
        assertEquals(17l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Superbuy at 2015
        statOverview = statService.overview(LocalDateTime.parse("2015-01-01T01:00:00"), LocalDateTime.parse("2015-12-31T01:00:00"), "SUPERBUY");
        assertEquals(10l, statOverview.getPageView().getOverall().longValue());
        assertEquals(1.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(2l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(1l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().longValue());
        assertEquals(7l, statOverview.getUniqueVisitor().getOverall().longValue());   
	}

	
}
