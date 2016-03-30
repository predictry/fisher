package com.predictry.fisher.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.fisher.domain.profile.UserProfileAction;
import com.predictry.fisher.domain.profile.UserProfileItem;
import com.predictry.fisher.domain.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {

    private static final Logger log = LoggerFactory.getLogger(HistoryService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileService userProfileService;

    public void process(List<String> sources, String tenantId, LocalDateTime expectedTime) {
        Map<String, String> userEmails = collectUserEmails(sources);
        sources.forEach(line -> {
            try {
                Map<String, Object> mapJson = objectMapper.readValue(line, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> data = Helper.getData(mapJson);
                String type = Helper.getDataName(mapJson);
                if ((data != null) && (type != null) && data.containsKey("user") && (data.get("user") != null) && userEmails.containsKey(data.get("user").toString())) {
                    String userId = (String) data.get("user");
                    String itemId = (String) data.get("item");
                    if (UserProfileAction.contains(type)) {
                        LocalDateTime time = ZonedDateTime.parse((String) data.get("timestamp")).toLocalDateTime();
                        UserProfileItem userProfileItem = new UserProfileItem(time, (String) data.get("tenant"));
                        userProfileItem.setAction(UserProfileAction.valueOf(type));
                        userProfileItem.setUserId(userId);
                        userProfileItem.setEmail(userEmails.get(userId));
                        userProfileItem.setItemId((String) data.get("item"));
                        userProfileItem.setAgentId((String) data.get("agent"));
                        userProfileService.save(userProfileItem);
                    }
                }
            } catch (IOException ex) {
                log.error("Failed to parse Json", ex);
            }
        });
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

}
