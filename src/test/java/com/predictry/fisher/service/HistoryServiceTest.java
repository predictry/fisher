package com.predictry.fisher.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.history.History;
import com.predictry.fisher.domain.history.HistoryItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader= AnnotationConfigContextLoader.class)
public class HistoryServiceTest {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void retrieveHistory() {
        LocalDate date = LocalDate.parse("2016-02-15");
        History history = historyService.retrieveHistory("latihan", date);
        assertEquals("latihan", history.getTenantId());
        assertEquals(date, history.getDate());
        assertEquals(4, history.getActivities().size());

        assertEquals("user1", history.getActivities().get("user1").getUserId());
        assertEquals("user1@gmail.com", history.getActivities().get("user1").getEmail());
        assertEquals(1, history.getActivities().get("user1").getBuys().size());
        assertTrue(history.getActivities().get("user1").getBuys().contains("item1"));
        assertEquals(4, history.getActivities().get("user1").getViews().size());
        assertTrue(history.getActivities().get("user1").getViews().contains("item1"));
        assertTrue(history.getActivities().get("user1").getViews().contains("item2"));
        assertTrue(history.getActivities().get("user1").getViews().contains("item3"));
        assertTrue(history.getActivities().get("user1").getViews().contains("item4"));

        assertEquals("user2", history.getActivities().get("user2").getUserId());
        assertEquals("user2@gmail.com", history.getActivities().get("user2").getEmail());
        assertEquals(1, history.getActivities().get("user2").getBuys().size());
        assertTrue(history.getActivities().get("user2").getBuys().contains("item2"));
        assertEquals(4, history.getActivities().get("user2").getViews().size());
        assertTrue(history.getActivities().get("user2").getViews().contains("item2"));
        assertTrue(history.getActivities().get("user2").getViews().contains("item3"));
        assertTrue(history.getActivities().get("user2").getViews().contains("item4"));
        assertTrue(history.getActivities().get("user2").getViews().contains("item5"));

        assertEquals("user3", history.getActivities().get("user3").getUserId());
        assertEquals("user3@gmail.com", history.getActivities().get("user3").getEmail());
        assertEquals(1, history.getActivities().get("user3").getBuys().size());
        assertTrue(history.getActivities().get("user3").getBuys().contains("item3"));
        assertEquals(2, history.getActivities().get("user3").getViews().size());
        assertTrue(history.getActivities().get("user3").getViews().contains("item3"));
        assertTrue(history.getActivities().get("user3").getViews().contains("item4"));

        assertEquals("user4", history.getActivities().get("user4").getUserId());
        assertEquals("user4@gmail.com", history.getActivities().get("user4").getEmail());
        assertEquals(1, history.getActivities().get("user4").getBuys().size());
        assertTrue(history.getActivities().get("user4").getBuys().contains("item4"));
        assertEquals(3, history.getActivities().get("user4").getViews().size());
        assertTrue(history.getActivities().get("user4").getViews().contains("item4"));
        assertTrue(history.getActivities().get("user4").getViews().contains("item5"));
        assertTrue(history.getActivities().get("user4").getViews().contains("item6"));
    }

    @Test
    public void retrieveEmptyHistory() {
        LocalDate date = LocalDate.parse("2016-02-17");
        History history = historyService.retrieveHistory("latihan", date);
        assertEquals("latihan", history.getTenantId());
        assertEquals(date, history.getDate());
        assertEquals(0, history.getActivities().size());
    }

    @Test
    public void collectUsers() throws IOException {
        File file = new File(getClass().getResource("/sample_email.log").getFile());
        List<String> sources = Files.readAllLines(file.toPath());
        Map<String, String> users = historyService.collectUserEmails(sources);
        assertEquals(9, users.size());
        assertEquals("beeling_low@yahoo.com", users.get("15142"));
        assertEquals("yyin33@yahoo.com", users.get("31868"));
        assertEquals("gr_1110@hotmail.com", users.get("11004"));
        assertEquals("ycsb_fpk@yahoo.com", users.get("31311"));
        assertEquals("norulfarina@splash.com.my", users.get("16132"));
        assertEquals("waynieong@yahoo.com", users.get("30345"));
        assertEquals("teohlaysim@yahoo.com", users.get("31626"));
    }

    @Test
    public void process() throws IOException {
        File file = new File(getClass().getResource("/sample_email.log").getFile());
        List<String> sources = Files.readAllLines(file.toPath());
        LocalDateTime date = LocalDateTime.parse("2016-02-16T02:00");
        History history = historyService.process(sources, "latihan", date);
        assertEquals("latihan", history.getTenantId());
        assertEquals(date.toLocalDate(), history.getDate());

        AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));

        // user 15142
        HistoryItem item = history.getHistoryItem("15142");
        assertEquals("15142", item.getUserId());
        assertEquals("beeling_low@yahoo.com", item.getEmail());
        assertEquals(2, item.getViews().size());
        assertTrue(item.getViews().contains("2284"));
        assertTrue(item.getViews().contains("168"));
        assertEquals(2, item.getBuys().size());
        assertTrue(item.getBuys().contains("168"));
        assertTrue(item.getBuys().contains("2571"));

        InputStream is = s3Client.getObject("predictry", "data/tenants/latihan/history/2016/02/16/15142.json").getObjectContent();
        HistoryItem itemFromS3 = objectMapper.readValue(is, HistoryItem.class);
        itemFromS3.setUserId("15142");
        assertEquals(item, itemFromS3);

        // user 31868
        item = history.getHistoryItem("31868");
        assertEquals("31868", item.getUserId());
        assertEquals("yyin33@yahoo.com", item.getEmail());
        assertEquals(2, item.getViews().size());
        assertTrue(item.getViews().contains("1660"));
        assertTrue(item.getViews().contains("2501"));
        assertEquals(0, item.getBuys().size());

        is = s3Client.getObject("predictry", "data/tenants/latihan/history/2016/02/16/31868.json").getObjectContent();
        itemFromS3 = objectMapper.readValue(is, HistoryItem.class);
        itemFromS3.setUserId("31868");
        assertEquals(item, itemFromS3);

        // user 11004
        item = history.getHistoryItem("11004");
        assertEquals("11004", item.getUserId());
        assertEquals("gr_1110@hotmail.com", item.getEmail());
        assertEquals(4, item.getViews().size());
        assertTrue(item.getViews().contains("587"));
        assertTrue(item.getViews().contains("394"));
        assertTrue(item.getViews().contains("315"));
        assertTrue(item.getViews().contains("2547"));
        assertEquals(0, item.getBuys().size());

        is = s3Client.getObject("predictry", "data/tenants/latihan/history/2016/02/16/11004.json").getObjectContent();
        itemFromS3 = objectMapper.readValue(is, HistoryItem.class);
        itemFromS3.setUserId("11004");
        assertEquals(item, itemFromS3);

        // user 31311
        item = history.getHistoryItem("31311");
        assertEquals("31311", item.getUserId());
        assertEquals("ycsb_fpk@yahoo.com", item.getEmail());
        assertEquals(1, item.getViews().size());
        assertTrue(item.getViews().contains("2129"));
        assertEquals(0, item.getBuys().size());

        is = s3Client.getObject("predictry", "data/tenants/latihan/history/2016/02/16/31311.json").getObjectContent();
        itemFromS3 = objectMapper.readValue(is, HistoryItem.class);
        itemFromS3.setUserId("31311");
        assertEquals(item, itemFromS3);

        // user 16132
        item = history.getHistoryItem("16132");
        assertEquals("16132", item.getUserId());
        assertEquals("norulfarina@splash.com.my", item.getEmail());
        assertEquals(1, item.getViews().size());
        assertTrue(item.getViews().contains("1446"));
        assertEquals(0, item.getBuys().size());

        is = s3Client.getObject("predictry", "data/tenants/latihan/history/2016/02/16/16132.json").getObjectContent();
        itemFromS3 = objectMapper.readValue(is, HistoryItem.class);
        itemFromS3.setUserId("16132");
        assertEquals(item, itemFromS3);

        // user 30345
        item = history.getHistoryItem("30345");
        assertEquals("30345", item.getUserId());
        assertEquals("waynieong@yahoo.com", item.getEmail());
        assertEquals(6, item.getViews().size());
        assertTrue(item.getViews().contains("2593"));
        assertTrue(item.getViews().contains("2585"));
        assertTrue(item.getViews().contains("2595"));
        assertTrue(item.getViews().contains("2591"));
        assertTrue(item.getViews().contains("2601"));
        assertTrue(item.getViews().contains("2600"));
        assertEquals(0, item.getBuys().size());

        is = s3Client.getObject("predictry", "data/tenants/latihan/history/2016/02/16/30345.json").getObjectContent();
        itemFromS3 = objectMapper.readValue(is, HistoryItem.class);
        itemFromS3.setUserId("30345");
        assertEquals(item, itemFromS3);

        // user 31626
        assertNull(history.getHistoryItem("31626"));
    }

}
