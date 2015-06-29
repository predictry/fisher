package com.predictry.fisher.domain.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;

@Document(indexName="top")
public class TopScore {
	
	public static final int MAX_NUMBER_OF_SCORES = 10;
	
	@Field(type=FieldType.Nested)
	private List<ItemScore> items = new ArrayList<>();

	@Id
	@JsonSerialize(using=JacksonTimeSerializer.class)
	@JsonDeserialize(using=JacksonTimeDeserializer.class)
	private LocalDateTime time;
	
	@JsonIgnore
	private String tenantId;
	
	public TopScore() {}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	
	public LocalDateTime getTime() {
		return time;
	}
	
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public List<ItemScore> getItems() {
		return items;
	}
	
	/**
	 * Get Elasticsearch index name in form of "top_yyyy" based on time.
	 * 
	 * @return index name for Elasticsearch.
	 */
	public String getIndexName() {
		Assert.notNull(time);
		return "top_" + getTime().getYear();
	}
	
	/**
	 * Add new score to existing score if this score is better than existing
	 * top ten score.
	 * 
	 * If id of the <code>Item</code> already exists in the score, this method 
	 * will increase the score of existing item and resort the score.
	 * 
	 * @param itemScore new score to put into top ten score.
	 */
	public void addNewScore(ItemScore itemScore) {
		// Validate againsts duplicated ids.
		if (items.stream().filter(i -> i.getId().equals(itemScore.getId())).findFirst().isPresent()) {
			throw new RuntimeException("Duplicate id in top score: [" + itemScore + "]");
		}
		
		if (items.isEmpty()) {
			// Items is empty, so this is the first score.
			items.add(itemScore);
		} else {
			// Not an existing item, but can it beat current top score?
			boolean added = false;
			for (int i=0; i<items.size(); i++) {
				if (itemScore.getScore() > items.get(i).getScore()) {
					items.add(i, itemScore);
					added = true;
					break;
				}
			}
			if (added) {
				if (items.size() > MAX_NUMBER_OF_SCORES) {
					items.remove(items.size() - 1);
				}
			} else {
				if (items.size() < MAX_NUMBER_OF_SCORES) {
					items.add(itemScore);
				}
			}
		}
	}
	
	/**
	 * Add new score to existing score if this score is better than existing
	 * top ten score.
	 * 
	 * @see #addNewScore(ItemScore) 
	 */
	public void addNewScore(String id, String name, String url, Double score) {
		addNewScore(new ItemScore(id, name, url, score));
	}
	
	/**
	 * Get an item at certain rank.
	 * 
	 * @param rank is the rank number.  The smallest number (<code>1</code>) indicates higher score.
	 * @return an <code>Item</code> or <code>null</code> if there is no available item for that rank.
	 */
	public ItemScore getItemAtRank(int rank) {
		int index = rank - 1;
		if ((index < 0) || (index >= items.size())) {
			return null;
		} else {
			return items.get(index);
		}
	}
 
}
