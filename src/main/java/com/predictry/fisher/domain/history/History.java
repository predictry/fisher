package com.predictry.fisher.domain.history;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class History {

	private String tenantId;
	private LocalDate date;
	private Map<String, HistoryItem> activities = new HashMap<>();

	public History(String tenantId, LocalDate date) {
		this.tenantId = tenantId;
		this.date = date;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Map<String, HistoryItem> getActivities() {
		return this.activities;
	}

	public HistoryItem getHistoryItem(String userId, String email) {
		HistoryItem historyItem;
		if (activities.containsKey(userId)) {
			historyItem = activities.get(userId);
			historyItem.setEmail(email);
		} else {
			historyItem = new HistoryItem(userId, email);
			activities.put(userId, historyItem);
		}
		return historyItem;
	}

    public HistoryItem getHistoryItem(String userId) {
        return activities.get(userId);
    }

	public void addViewActivity(String userId, String email, String itemId) {
		getHistoryItem(userId, email).addViewItem(itemId);
	}

	public void addBuyActivity(String userId, String email, String itemId) {
		getHistoryItem(userId, email).addBuyItem(itemId);
	}

	public void populateActivities(Set<HistoryItem> items) {
		activities.clear();
		items.forEach(historyItem -> activities.put(historyItem.getUserId(), historyItem));
	}

	public void addActivity(HistoryItem item) {
		activities.put(item.getUserId(), item);
	}

}
