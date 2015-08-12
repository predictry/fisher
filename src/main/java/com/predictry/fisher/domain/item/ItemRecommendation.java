package com.predictry.fisher.domain.item;

import java.util.ArrayList;
import java.util.List;

public class ItemRecommendation {

	private String algo = "similiar";
	
	private List<String> items = new ArrayList<>();

	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
	}

	public List<String> getItems() {
		return items;
	}

	public void addItem(String itemId) {
		this.items.add(itemId);
	}
	
}
