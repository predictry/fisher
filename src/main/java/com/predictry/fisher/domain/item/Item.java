package com.predictry.fisher.domain.item;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(indexName="item")
public class Item {

	@Id
	private String id;
	private String name;
	private String itemUrl;
	private String imageUrl;
	private String category;
	@JsonIgnore
	private String tenantId;
	
	public Item() {}
	
	public Item(String id, String name, String itemUrl, String imageUrl, String category) {
		this.id = id;
		this.name = name;
		this.itemUrl = itemUrl;
		this.imageUrl = imageUrl;
		this.category = category;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getItemUrl() {
		return itemUrl;
	}
	
	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getIndexName() {
		Assert.notNull(getTenantId());
		return "item_" + getTenantId().toLowerCase();
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", itemUrl=" + itemUrl
				+ ", imageUrl=" + imageUrl + ", category=" + category + "]";
	}
	
}
