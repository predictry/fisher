package com.predictry.fisher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.config.TestRootConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

}
