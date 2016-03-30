package com.predictry.fisher.service;

import com.predictry.fisher.config.TestRootConfig;
import com.predictry.fisher.domain.profile.UserProfile;
import com.predictry.fisher.domain.profile.UserProfileAction;
import com.predictry.fisher.domain.profile.UserProfileItem;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestRootConfig.class}, loader= AnnotationConfigContextLoader.class)
public class UserProfileTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserProfileService userProfileService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ElasticsearchTemplate template;

    @Before
    public void clean() {
        if (template.indexExists("history_2016")) {
            template.deleteIndex("history_2016");
        }
    }

    @Test
    public void createUserProfile() throws IOException {
        // User profile should be created as a side effect of history processing.
        File file = new File(getClass().getResource("/sample_email.log").getFile());
        List<String> sources = Files.readAllLines(file.toPath());
        LocalDateTime date = LocalDateTime.parse("2016-02-16T02:00");
        historyService.process(sources, "latihan", date);

        // Check Elasticsearch
        template.refresh("history_2016", true);
        assertTrue(template.indexExists("history_2016"));
        //assertTrue(template.typeExists("history_2016", "latihan"));

        // Check if row is created
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("history_2016").withTypes("BANYAKDEALCOM")
            .withQuery(new MatchAllQueryBuilder()).withPageable(new PageRequest(0, 100)).build();
        List<UserProfileItem> searchResult = template.queryForList(searchQuery, UserProfileItem.class);
        assertEquals(24, searchResult.size());
    }

    @Test
    public void getUserProfile() throws IOException {
        // Import user profile data to Elasticsearch
        File file = new File(getClass().getResource("/sample_user_profile.log").getFile());
        List<String> sources = Files.readAllLines(file.toPath());
        LocalDateTime date = LocalDateTime.parse("2016-02-16T02:00");
        historyService.process(sources, "latihan", date);
        template.refresh("history_2016", true);

        // Search for buy history
        UserProfile userProfile = userProfileService.getUserProfile("BANYAKDEALCOM", "15142", UserProfileAction.BUY);
        assertEquals(LocalDateTime.parse("2016-02-19T02:45:09"), userProfile.getLastAction());
        assertEquals(2, userProfile.getItems().size());
        assertEquals(LocalDateTime.parse("2016-02-19T02:45:09"), userProfile.getItems().get(0).getTime());
        assertEquals(UserProfileAction.BUY, userProfile.getItems().get(0).getAction());
        assertEquals("15142", userProfile.getItems().get(0).getUserId());
        assertEquals("beeling_low@yahoo.com", userProfile.getItems().get(0).getEmail());
        assertEquals("2571", userProfile.getItems().get(0).getItemId());
        assertEquals("UEylygBw-lt5c-NHz8-mIQc-izANkRXxzWcn", userProfile.getItems().get(0).getAgentId());
        assertEquals(LocalDateTime.parse("2016-02-06T02:50:17"), userProfile.getItems().get(1).getTime());
        assertEquals(UserProfileAction.BUY, userProfile.getItems().get(1).getAction());
        assertEquals("15142", userProfile.getItems().get(1).getUserId());
        assertEquals("beeling_low@yahoo.com", userProfile.getItems().get(1).getEmail());
        assertEquals("168", userProfile.getItems().get(1).getItemId());
        assertEquals("UEylygBw-lt5c-NHz8-mIQc-izANkRXxzWcn", userProfile.getItems().get(1).getAgentId());

        // Search for view history
        userProfile = userProfileService.getUserProfile("BANYAKDEALCOM", "15142", UserProfileAction.VIEW);
        assertEquals(LocalDateTime.parse("2016-02-11T02:51:30"), userProfile.getLastAction());
        assertEquals(3, userProfile.getItems().size());
        assertEquals(LocalDateTime.parse("2016-02-11T02:51:30"), userProfile.getItems().get(0).getTime());
        assertEquals(UserProfileAction.VIEW, userProfile.getItems().get(0).getAction());
        assertEquals("15142", userProfile.getItems().get(0).getUserId());
        assertEquals("beeling_low@yahoo.com", userProfile.getItems().get(0).getEmail());
        assertEquals("2284", userProfile.getItems().get(0).getItemId());
        assertEquals("UEylygBw-lt5c-NHz8-mIQc-izANkRXxzWcn", userProfile.getItems().get(0).getAgentId());
        assertEquals(LocalDateTime.parse("2016-02-02T02:47:48"), userProfile.getItems().get(1).getTime());
        assertEquals(UserProfileAction.VIEW, userProfile.getItems().get(1).getAction());
        assertEquals("15142", userProfile.getItems().get(1).getUserId());
        assertEquals("beeling_low@yahoo.com", userProfile.getItems().get(1).getEmail());
        assertEquals("168", userProfile.getItems().get(1).getItemId());
        assertEquals("UEylygBw-lt5c-NHz8-mIQc-izANkRXxzWcn", userProfile.getItems().get(1).getAgentId());
        assertEquals(LocalDateTime.parse("2016-02-01T02:47:29"), userProfile.getItems().get(2).getTime());
        assertEquals(UserProfileAction.VIEW, userProfile.getItems().get(2).getAction());
        assertEquals("15142", userProfile.getItems().get(2).getUserId());
        assertEquals("beeling_low@yahoo.com", userProfile.getItems().get(2).getEmail());
        assertEquals("2284", userProfile.getItems().get(2).getItemId());
        assertEquals("UEylygBw-lt5c-NHz8-mIQc-izANkRXxzWcn", userProfile.getItems().get(2).getAgentId());

        // Search for buy history from another user
        userProfile = userProfileService.getUserProfile("BANYAKDEALCOM", "31311", UserProfileAction.BUY);
        assertNull(userProfile.getLastAction());
        assertEquals(0, userProfile.getItems().size());

        // Search for view history from another user
        userProfile = userProfileService.getUserProfile("BANYAKDEALCOM", "31311", UserProfileAction.VIEW);
        assertEquals(LocalDateTime.parse("2016-02-15T02:56:39"), userProfile.getLastAction());
        assertEquals(1, userProfile.getItems().size());
        assertEquals(LocalDateTime.parse("2016-02-15T02:56:39"), userProfile.getItems().get(0).getTime());
        assertEquals(UserProfileAction.VIEW, userProfile.getItems().get(0).getAction());
        assertEquals("31311", userProfile.getItems().get(0).getUserId());
        assertEquals("ycsb_fpk@yahoo.com", userProfile.getItems().get(0).getEmail());
        assertEquals("2129", userProfile.getItems().get(0).getItemId());
        assertEquals("LRqFASXb-o5Nk-C3tJ-ueTK-U7V488v86NIb", userProfile.getItems().get(0).getAgentId());

        // Search for unknown user
        userProfile = userProfileService.getUserProfile("BANYAKDEALCOM", "unknown_user", UserProfileAction.VIEW);
        assertNull(userProfile.getLastAction());
        assertEquals(0, userProfile.getItems().size());
    }

}
