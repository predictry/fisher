package com.predictry.fisher.controller;

import com.predictry.fisher.domain.profile.UserProfile;
import com.predictry.fisher.domain.profile.UserProfileAction;
import com.predictry.fisher.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.Map;

/**
 * REST API for user profile related operations.
 */
@RestController
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping("/userprofile/{tenantId}/{userId}/{action}")
    public UserProfile userProfile(@PathVariable String tenantId, @PathVariable String userId, @PathVariable String action) {
        log.info("Processing user profile for user [" + userId + "] tenant [" + tenantId + "] action [" + action + "]");
        return userProfileService.getUserProfile(tenantId, userId, UserProfileAction.valueOf(action.toUpperCase()));
    }

    @RequestMapping("/history/{tenantId}/{userId}/{action}")
    public Map<String, Object> history(@PathVariable String tenantId, @PathVariable String userId, @PathVariable String action,
                                       @RequestParam @DateTimeFormat(pattern="yyyyMMdd") LocalDate date) {
        log.info("Retrieving history for date [" + date + "]");
        return userProfileService.getHistory(tenantId, userId, UserProfileAction.valueOf(action.toUpperCase()), date);
    }

}
