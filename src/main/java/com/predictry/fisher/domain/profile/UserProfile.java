package com.predictry.fisher.domain.profile;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    @JsonSerialize(using=JacksonTimeSerializer.class)
    @JsonDeserialize(using=JacksonTimeDeserializer.class)
    private LocalDateTime lastAction;

    private List<UserProfileItem> items = new ArrayList<>();

    public LocalDateTime getLastAction() {
        return lastAction;
    }

    public void setLastAction(LocalDateTime lastAction) {
        this.lastAction = lastAction;
    }

    public void addAction(UserProfileItem userProfileItem) {
        this.items.add(userProfileItem);
    }

    public List<UserProfileItem> getItems() {
        return this.items;
    }

}
