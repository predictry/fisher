package com.predictry.fisher.domain.history;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HistoryTest {

    @Test
    public void getHistoryItem() {
        History history = new History("tenant1", LocalDate.now());
        HistoryItem item1 = history.getHistoryItem("user1", "user@email.com");
        assertEquals("user1", item1.getUserId());
        assertEquals("user@email.com", item1.getEmail());
        HistoryItem item2 = history.getHistoryItem("user1", "new@email.com");
        assertEquals(item1,item2);
        assertEquals("user1", item2.getUserId());
        assertEquals("new@email.com", item2.getEmail());
    }

    @Test
    public void addViewActivity() {
        History history = new History("tenant1", LocalDate.now());
        history.addViewActivity("user1", "user@email.com", "item1");
        history.addViewActivity("user1", "user@email.com", "item2");
        history.addViewActivity("user1", "user@email.com", "item1");
        history.addViewActivity("user2", "user@email.com", "item1");
        history.addViewActivity("user2", "user@email.com", "item2");
        history.addViewActivity("user3", "user@email.com", "item3");

        HistoryItem item1 = history.getHistoryItem("user1", "user@email.com");
        assertEquals(2, item1.getViews().size());
        assertTrue(item1.getViews().contains("item1"));
        assertTrue(item1.getViews().contains("item2"));

        HistoryItem item2 = history.getHistoryItem("user2", "user@email.com");
        assertEquals(2, item2.getViews().size());
        assertTrue(item2.getViews().contains("item1"));
        assertTrue(item2.getViews().contains("item2"));

        HistoryItem item3 = history.getHistoryItem("user3", "user@email.com");
        assertEquals(1, item3.getViews().size());
        assertTrue(item3.getViews().contains("item3"));
    }

    @Test
    public void addBuyActivity() {
        History history = new History("tenant1", LocalDate.now());
        history.addBuyActivity("user1", "user@email.com", "item1");
        history.addBuyActivity("user1", "user@email.com", "item2");
        history.addBuyActivity("user1", "user@email.com", "item1");
        history.addBuyActivity("user2", "user@email.com", "item1");
        history.addBuyActivity("user2", "user@email.com", "item2");
        history.addBuyActivity("user3", "user@email.com", "item3");

        HistoryItem item1 = history.getHistoryItem("user1", "user@email.com");
        assertEquals(2, item1.getBuys().size());
        assertTrue(item1.getBuys().contains("item1"));
        assertTrue(item1.getBuys().contains("item2"));

        HistoryItem item2 = history.getHistoryItem("user2", "user@email.com");
        assertEquals(2, item2.getBuys().size());
        assertTrue(item2.getBuys().contains("item1"));
        assertTrue(item2.getBuys().contains("item2"));

        HistoryItem item3 = history.getHistoryItem("user3", "user@email.com");
        assertEquals(1, item3.getBuys().size());
        assertTrue(item3.getBuys().contains("item3"));
    }

    @Test
    public void populateActivities() {
        Set<HistoryItem> items = new HashSet<>();
        items.add(new HistoryItem("user1", "user1@email.com"));
        items.add(new HistoryItem("user2", "user2@email.com"));
        items.add(new HistoryItem("user3", "user3@email.com"));
        History history = new History("tenant1", LocalDate.now());
        history.populateActivities(items);

        assertEquals(3, history.getActivities().size());
        assertEquals("user1", history.getActivities().get("user1").getUserId());
        assertEquals("user1@email.com", history.getActivities().get("user1").getEmail());
        assertEquals("user2", history.getActivities().get("user2").getUserId());
        assertEquals("user2@email.com", history.getActivities().get("user2").getEmail());
        assertEquals("user3", history.getActivities().get("user3").getUserId());
        assertEquals("user3@email.com", history.getActivities().get("user3").getEmail());
    }

}
