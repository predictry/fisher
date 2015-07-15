package com.predictry.fisher.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
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
import com.predictry.fisher.domain.stat.Metric;
import com.predictry.fisher.domain.stat.Stat;
import com.predictry.fisher.domain.stat.StatEntry;
import com.predictry.fisher.domain.stat.StatOverview;
import com.predictry.fisher.domain.stat.Value;

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
		Stat stat = new Stat("2015-12-15T10:00:00", "BUKALAPAK", 10.0, 1000.0, 20.0, 10.0, 5.0);
		statService.save(stat);
		
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
		assertEquals(10l, savedStat.getViews().getOverall().longValue());
		assertEquals(1000.0, savedStat.getSales().getOverall().doubleValue(), 0.1);
		assertEquals(20l, savedStat.getItemPurchased().getOverall().longValue());
		assertEquals(10l, savedStat.getOrders().getOverall().longValue());
		assertEquals(5l, savedStat.getUniqueVisitor().getOverall().longValue());
	}
	
	@Test
	public void updateExisting() {
		// Create new stat
		Stat stat = new Stat("2015-12-15T10:00:00", "BUKALAPAK", 10.0, 1000.0, 20.0, 10.0, 5.0);
		statService.save(stat);
		
		// Update this stat
		stat.addViews(new Value(200.0, 0.0, 0.0));
		statService.save(stat);
		
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
		assertEquals(210l, savedStat.getViews().getOverall().longValue());
		assertEquals(1000.0, savedStat.getSales().getOverall().doubleValue(), 0.1);
		assertEquals(20l, savedStat.getItemPurchased().getOverall().longValue());
		assertEquals(10l, savedStat.getOrders().getOverall().longValue());
		assertEquals(5l, savedStat.getUniqueVisitor().getOverall().longValue());
	}
	
	@Test
	public void statBucket() {
		// Create dummy data
		template.createIndex("stat_2015");
		template.createIndex("stat_2014");
		template.refresh("stat_2015", true);
		template.refresh("stat_2014", true);
		
		IndexQuery idxStat1 = new IndexQuery();
		Stat stat1 = new Stat("2014-01-01T01:00:00", "BUKALAPAK", 100.0, 10.0, 20.0, 1.0, 90.0);
		idxStat1.setIndexName("stat_2014");
		idxStat1.setType("BUKALAPAK");
		idxStat1.setId(stat1.getTime());
		idxStat1.setObject(stat1);
		
		IndexQuery idxStat2 = new IndexQuery();
		Stat stat2 = new Stat("2014-01-01T02:00:00", "BUKALAPAK", 200.0, 5.0, 10.0, 2.0, 80.0);
		idxStat2.setIndexName("stat_2014");
		idxStat2.setType("BUKALAPAK");
		idxStat2.setId(stat2.getTime());
		idxStat2.setObject(stat2);
		
		IndexQuery idxStat3 = new IndexQuery();
		Stat stat3 = new Stat("2015-02-01T10:00:00", "BUKALAPAK", 50.0, 3.0, 15.0, 3.0, 70.0);
		idxStat3.setIndexName("stat_2015");
		idxStat3.setType("BUKALAPAK");
		idxStat3.setId(stat3.getTime());
		idxStat3.setObject(stat3);
		
		IndexQuery idxStat4 = new IndexQuery();
		Stat stat4 = new Stat("2015-03-01T11:00:00", "BUKALAPAK", 30.0, 2.0, 13.0, 2.0, 60.0);
		idxStat4.setIndexName("stat_2015");
		idxStat4.setType("BUKALAPAK");
		idxStat4.setId(stat4.getTime());
		idxStat4.setObject(stat4);
		
		IndexQuery idxStat5 = new IndexQuery();
		Stat stat5 = new Stat("2014-01-01T01:00:00", "SUPERBUY", 10.0, 1.0, 2.0, 1.0, 9.0);
		idxStat5.setIndexName("stat_2014");
		idxStat5.setType("SUPERBUY");
		idxStat5.setId(stat5.getTime());
		idxStat5.setObject(stat5);
		
		IndexQuery idxStat6 = new IndexQuery();
		Stat stat6 = new Stat("2014-01-01T02:00:00", "SUPERBUY", 20.0, 3.0, 1.0, 2.0, 8.0);
		idxStat6.setIndexName("stat_2014");
		idxStat6.setType("SUPERBUY");
		idxStat6.setId(stat6.getTime());
		idxStat6.setObject(stat6);
		
		IndexQuery idxStat7 = new IndexQuery();
		Stat stat7 = new Stat("2014-01-02T10:00:00", "SUPERBUY", 10.0, 1.0, 1.0, 3.0, 7.0);
		idxStat7.setIndexName("stat_2014");
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

        // Stat for Bukalapak hourly from 2014-01-01T01:00:00 to 2015-02-01T10:00:00
        List<StatEntry> result = statService.stat(LocalDateTime.parse("2014-01-01T01:00:00"), 
    		LocalDateTime.parse("2014-01-01T05:00:00"), "BUKALAPAK", Metric.VIEWS, DateHistogram.Interval.HOUR);
        assertEquals(2, result.size());
        assertEquals(LocalDateTime.parse("2014-01-01T01:00:00"), result.get(0).getDate());
        assertEquals(100.0, result.get(0).getValue(), 0.1);
        assertEquals(LocalDateTime.parse("2014-01-01T02:00:00"), result.get(1).getDate());
        assertEquals(200.0, result.get(1).getValue(), 0.1);
        
        // Stat for Bukalapak daily from 2014-01-01T01:00:00 to 2016-01-01T10:00:00
        result = statService.stat(LocalDateTime.parse("2014-01-01T01:00:00"), 
    		LocalDateTime.parse("2014-01-03T10:00:00"), "BUKALAPAK", Metric.VIEWS, DateHistogram.Interval.DAY);
        assertEquals(1, result.size());
        assertEquals(LocalDateTime.parse("2014-01-01T00:00:00"), result.get(0).getDate());
        assertEquals(300.0, result.get(0).getValue(), 0.1);
        
        // Stat for Superbuy daily from 2014-01-01T01:00:00 to 2015-12-31T10:00:00
        result = statService.stat(LocalDateTime.parse("2014-01-01T01:00:00"), 
    		LocalDateTime.parse("2014-01-03T10:00:00"), "SUPERBUY", Metric.VIEWS, DateHistogram.Interval.DAY);
        assertEquals(2, result.size());
        assertEquals(LocalDateTime.parse("2014-01-01T00:00:00"), result.get(0).getDate());
        assertEquals(30.0, result.get(0).getValue(), 0.1);
        assertEquals(LocalDateTime.parse("2014-01-02T00:00:00"), result.get(1).getDate());
        assertEquals(10.0, result.get(1).getValue(), 0.1);
	}
	
	@Test
	public void statOverview() {
		// Create dummy data
		template.createIndex("stat_2015");
		template.createIndex("stat_2014");
		template.refresh("stat_2015", true);
		template.refresh("stat_2014", true);
		
		IndexQuery idxStat1 = new IndexQuery();
		Stat stat1 = new Stat("2014-01-01T01:00:00", "BUKALAPAK", 100.0, 10.0, 20.0, 1.0, 90.0);
		idxStat1.setIndexName("stat_2014");
		idxStat1.setType("BUKALAPAK");
		idxStat1.setId(stat1.getTime());
		idxStat1.setObject(stat1);
		
		IndexQuery idxStat2 = new IndexQuery();
		Stat stat2 = new Stat("2014-01-01T02:00:00", "BUKALAPAK", 200.0, 5.0, 10.0, 2.0, 80.0);
		idxStat2.setIndexName("stat_2014");
		idxStat2.setType("BUKALAPAK");
		idxStat2.setId(stat2.getTime());
		idxStat2.setObject(stat2);
		
		IndexQuery idxStat3 = new IndexQuery();
		Stat stat3 = new Stat("2015-02-01T10:00:00", "BUKALAPAK", 50.0, 3.0, 15.0, 3.0, 70.0);
		idxStat3.setIndexName("stat_2015");
		idxStat3.setType("BUKALAPAK");
		idxStat3.setId(stat3.getTime());
		idxStat3.setObject(stat3);
		
		IndexQuery idxStat4 = new IndexQuery();
		Stat stat4 = new Stat("2015-03-01T11:00:00", "BUKALAPAK", 30.0, 2.0, 13.0, 2.0, 60.0);
		idxStat4.setIndexName("stat_2015");
		idxStat4.setType("BUKALAPAK");
		idxStat4.setId(stat4.getTime());
		idxStat4.setObject(stat4);
		
		IndexQuery idxStat5 = new IndexQuery();
		Stat stat5 = new Stat("2014-01-01T01:00:00", "BUKALAPAK", 10.0, 1.0, 2.0, 1.0, 9.0);
		idxStat5.setIndexName("stat_2014");
		idxStat5.setType("SUPERBUY");
		idxStat5.setId(stat5.getTime());
		idxStat5.setObject(stat5);
		
		IndexQuery idxStat6 = new IndexQuery();
		Stat stat6 = new Stat("2014-01-01T02:00:00", "BUKALAPAK", 20.0, 3.0, 1.0, 2.0, 8.0);
		idxStat6.setIndexName("stat_2014");
		idxStat6.setType("SUPERBUY");
		idxStat6.setId(stat6.getTime());
		idxStat6.setObject(stat6);
		
		IndexQuery idxStat7 = new IndexQuery();
		Stat stat7 = new Stat("2015-02-01T10:00:00", "BUKALAPAK", 10.0, 1.0, 1.0, 3.0, 7.0);
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
        assertEquals(10l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(30l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().getOverall().longValue());
        assertEquals(170l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Bukalapak at 2015 month 2
        statOverview = statService.overview(LocalDateTime.parse("2015-02-01T01:00:00"), LocalDateTime.parse("2015-02-10T01:00:00"), "BUKALAPAK");
        assertEquals(50l, statOverview.getPageView().getOverall().longValue());
        assertEquals(3.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(5l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(15l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().getOverall().longValue());
        assertEquals(70l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Bukalapak at 2014-2015
        statOverview = statService.overview(LocalDateTime.parse("2014-01-01T01:00:00"), LocalDateTime.parse("2015-12-31T01:00:00"), "BUKALAPAK");
        assertEquals(380l, statOverview.getPageView().getOverall().longValue());
        assertEquals(20.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(7l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(58l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(8l, statOverview.getOrders().getOverall().longValue());
        assertEquals(300l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Superbuy at 2014
        statOverview = statService.overview(LocalDateTime.parse("2014-01-01T01:00:00"), LocalDateTime.parse("2014-12-31T01:00:00"), "SUPERBUY");
        assertEquals(30l, statOverview.getPageView().getOverall().longValue());
        assertEquals(4.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(1l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(3l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().getOverall().longValue());
        assertEquals(17l, statOverview.getUniqueVisitor().getOverall().longValue());
        
        // Stat for Superbuy at 2015
        statOverview = statService.overview(LocalDateTime.parse("2015-01-01T01:00:00"), LocalDateTime.parse("2015-12-31T01:00:00"), "SUPERBUY");
        assertEquals(10l, statOverview.getPageView().getOverall().longValue());
        assertEquals(1.0, statOverview.getSalesAmount().getOverall(), 0.5);
        assertEquals(0l, statOverview.getItemPerCart().getOverall().longValue());
        assertEquals(1l, statOverview.getItemPurchased().getOverall().longValue());
        assertEquals(3l, statOverview.getOrders().getOverall().longValue());
        assertEquals(7l, statOverview.getUniqueVisitor().getOverall().longValue());   
	}

	
}
