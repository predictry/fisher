package com.predictry.fisher.domain.history;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;

public class HistoryItem {

    @JsonIgnore
    private String userId;
    private String email;
    private HashSet<String> buys = new HashSet<>();
    private HashSet<String> views = new HashSet<>();

    public HistoryItem() {}

    public HistoryItem(String userId) {
        this.userId = userId;
    }

    public HistoryItem(String userId, String email) {
        this.userId = userId;
        this.email = email;
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

    public HashSet<String> getBuys() {
        return buys;
    }

    public void addBuyItem(String itemId) {
        this.buys.add(itemId);
    }

    public HashSet<String> getViews() {
        return views;
    }

    public void addViewItem(String itemId) {
        this.views.add(itemId);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return buys.isEmpty() && views.isEmpty();
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryItem that = (HistoryItem) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (buys != null ? !buys.equals(that.buys) : that.buys != null) return false;
        return views != null ? views.equals(that.views) : that.views == null;

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (buys != null ? buys.hashCode() : 0);
        result = 31 * result + (views != null ? views.hashCode() : 0);
        return result;
    }

}
