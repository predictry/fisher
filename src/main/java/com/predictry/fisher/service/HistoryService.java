package com.predictry.fisher.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.predictry.fisher.domain.history.History;
import com.predictry.fisher.domain.history.HistoryItem;
import com.predictry.fisher.domain.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {

    private static final Logger log = LoggerFactory.getLogger(HistoryService.class);

    @Autowired
    private ObjectMapper objectMapper;

    public History process(List<String> sources, String tenantId, LocalDateTime expectedTime) {
        Map<String, String> userEmails = collectUserEmails(sources);
        History history = retrieveHistory(tenantId, expectedTime.toLocalDate());
        sources.forEach(line -> {
            try {
                Map<String, Object> mapJson = objectMapper.readValue(line, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> data = Helper.getData(mapJson);
                String type = Helper.getDataName(mapJson);
                if ((data != null) && (type != null) && data.containsKey("user") && (data.get("user") != null) && userEmails.containsKey(data.get("user").toString())) {
                    String userId = (String) data.get("user");
                    String itemId = (String) data.get("item");
                    if (type.equals("VIEW")) {
                        history.addViewActivity(userId, userEmails.get(userId), itemId);
                    } else if (type.equals("BUY")) {
                        history.addBuyActivity(userId, userEmails.get(userId), itemId);
                    }
                }
            } catch (IOException ex) {
                log.error("Failed to parse Json", ex);
            }
        });
        pushToS3(history);
        return history;
    }

    public Map<String, String> collectUserEmails(List<String> sources) {
        log.info("Collecting email addresses");
        Map<String, String> result = new HashMap<>();
        sources.forEach(line -> {
            try {
                Map<String, Object> mapJson = objectMapper.readValue(line, new TypeReference<Map<String, Object>>(){});
                if (mapJson.containsKey("type") && mapJson.get("type").equals("User")) {
                    String id = (String) Helper.getData(mapJson).get("id");
                    String email = (String) Helper.getFieldValue(mapJson, "email");
                    if (email != null) {
                        result.put(id, email);
                    }
                }
            } catch (IOException ex) {
                log.error("Failed to parse Json", ex);
            }
        });
        return result;
    }

    public History retrieveHistory(String tenantId, LocalDate date) {
        log.info("Retrieving history from S3 for " + tenantId + " at " + date);
        History history = new History(tenantId, date);
        AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
        String key = String.format("data/tenants/%s/history/%s/%02d/%02d/", tenantId, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName("predictry").withPrefix(key);
        ObjectListing objectListing;
        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            objectListing.getObjectSummaries().forEach(objectSummary -> {
                if (!objectSummary.getKey().equals(key)) {
                    GetObjectRequest getRequest = new GetObjectRequest("predictry", objectSummary.getKey());
                    try {
                        HistoryItem historyItem = objectMapper.readValue(s3Client.getObject(getRequest).getObjectContent(), HistoryItem.class);
                        String userId = objectSummary.getKey().substring(key.length(), objectSummary.getKey().length() - 5);
                        historyItem.setUserId(userId);
                        history.addActivity(historyItem);
                    } catch (IOException ex) {
                        log.error("Failed to read file from S3", ex);
                    }
                }
            });
        } while (objectListing.isTruncated());

        return history;
    }

    public void pushToS3(History history) {
        log.info("Pushing and syncing history to S3");
        AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
        history.getActivities().forEach((userId, historyItem) -> {
            if (!historyItem.isEmpty()) {
                try {
                    byte[] content = objectMapper.writeValueAsString(historyItem).getBytes(StringUtils.UTF8);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(content.length);
                    String key = String.format("data/tenants/%s/history/%s/%02d/%02d/%s.json", history.getTenantId(), history.getDate().getYear(),
                        history.getDate().getMonthValue(), history.getDate().getDayOfMonth(), userId);
                    PutObjectRequest request = new PutObjectRequest("predictry", key, new ByteArrayInputStream(content), metadata);
                    s3Client.putObject(request);
                } catch (JsonProcessingException ex) {
                    log.error("Failed to create Json for [" + historyItem + "]", ex);
                }
            }
        });
    }

}
