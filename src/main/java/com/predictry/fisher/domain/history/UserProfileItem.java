package com.predictry.fisher.domain.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Document(indexName="userProfile")
public class UserProfileItem {

    @JsonSerialize(using=JacksonTimeSerializer.class)
    @JsonDeserialize(using=JacksonTimeDeserializer.class)
    private LocalDateTime time;

    private UserProfileAction action;
    private String userId;
    private String email;
    private String itemId;
    private String agentId;

    @JsonIgnore
    private String tenantId;

    public UserProfileItem() {}

    public UserProfileItem(LocalDateTime time, String tenantId) {
        this.time = time;
        this.tenantId = tenantId;
    }

    public String getIndexName() {
        Assert.notNull(time);
        return String.format("%s_%s", "history", time.getYear());
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public UserProfileAction getAction() {
        return action;
    }

    public void setAction(UserProfileAction action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

}
