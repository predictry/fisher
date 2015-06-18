package com.predictry.fisher.domain.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName="top", type="topScore")
public class TopScore<T extends Number> {
	/*
	public static final String TOP_SALES = "TopSales";
	public static final String TOP_HIT = "TopHit";
	
	private List<Item<T>> items = new ArrayList<>();

	@Id
	private String type;
	
	public TopScore() {}

	public TopScore(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Item<T>> getItems() {
		return items;
	}
	*/
	
	/**
	 * Add new score to existing score if this score is better than existing
	 * top ten score.
	 * 
	 * @param itemScore new score to put into top ten score.
	 */
	/*
	public void addNewScore(Item<T> itemScore) {
		if (items.isEmpty()) {
			items.add(itemScore);
		} else {
			boolean added = false;
			for (int i=0; i<items.size(); i++) {
				if (itemScore.getScore().doubleValue() > items.get(i).getScore().doubleValue()) {
					items.add(i, itemScore);
					added = true;
					break;
				}
			}
			if (added && (items.size() > 10)) {
				items.remove(items.size() - 1);
			}
		}
	}
	*/
	/**
	 * Add new score to existing score if this score is better than existing
	 * top ten score.
	 * 
	 * @see #addNewScore(Item) 
	 */
	/*
	public void addNewScore(String id, String name, String url, T score) {
		addNewScore(new Item<T>(id, name, url, score));
	}
	*/
}
