package com.predictry.fisher.domain.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ScoreStore {

	private Map<String, List<ItemScore>> data = new HashMap<>();
	
	public void add(String tenantId, ItemScore itemScore) {
		List<ItemScore> scores = getStore(tenantId);
		Optional<ItemScore> optItemScore = scores.stream().filter(i -> i.getId().equals(itemScore.getId())).findFirst();
		if (optItemScore.isPresent()) {
			optItemScore.get().increaseScore(itemScore.getScore());
		} else {
			scores.add(itemScore);
		}
	}
	
	public List<ItemScore> getStore(String tenantId) {
		if (data.containsKey(tenantId)) {
			return data.get(tenantId);
		} else {
			List<ItemScore> scores = new ArrayList<>();
			data.put(tenantId, scores);
			return scores;
		}
	}
	
	public Map<String, List<ItemScore>> getData() {
		return this.data;
	}
	
}
