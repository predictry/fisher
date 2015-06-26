package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(indexName="stat")
public class Stat {
	
	private String time;
	@JsonIgnore
	private String tenantId;
	private Long views = 0l;
	private Double sales = 0.0;
	private Long itemPurchased = 0l;
	private Long orders = 0l;
	private Long uniqueVisitor = 0l;
		
	public Stat() {}
	
	public Stat(String time, String tenantId, Long views, Double sales,
			Long itemPurchased, Long orders, Long uniqueVisitor) {
		super();
		this.time = time;
		this.tenantId = tenantId;
		this.views = views;
		this.sales = sales;
		this.itemPurchased = itemPurchased;
		this.orders = orders;
		this.uniqueVisitor = uniqueVisitor;
	}

	@Id
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setTimeFrom(LocalDateTime time) {
		this.time = time.toString();
	}
	
	public Long getViews() {
		return views;
	}
	
	public void setViews(Long views) {
		this.views = views;
	}
	
	public Double getSales() {
		return sales;
	}
	
	public void setSales(Double sales) {
		this.sales = sales;
	}
		
	public Long getItemPurchased() {
		return itemPurchased;
	}
	
	public void setItemPurchased(Long itemPurchased) {
		this.itemPurchased = itemPurchased;
	}
	
	public Long getOrders() {
		return orders;
	}
	
	public void setOrders(Long orders) {
		this.orders = orders;
	}
	
	public Long getUniqueVisitor() {
		return uniqueVisitor;
	}
	
	public void setUniqueVisitor(Long uniqueVisitor) {
		this.uniqueVisitor = uniqueVisitor;
	}
	
	public LocalDateTime time() {
		return LocalDateTime.parse(time);
	}
	
	/**
	 * Get Elasticsearch index name in form of "stat_yyyy" based on time.
	 * 
	 * @return index name for Elasticsearch.
	 */
	public String getIndexName() {
		Assert.notNull(time);
		return "stat_" + time().getYear();
	}
	
	/**
	 * Check if a time is in the range of this stat.
	 * 
	 * @param anotherTime a <code>LocalDateTime</code> to check for.
	 * @return <code>true</code> if the time is in this range.
	 */
	public boolean isForTime(LocalDateTime anotherTime) {
		if ((time == null) || (anotherTime == null)) return false;
		if (!time().toLocalDate().isEqual(anotherTime.toLocalDate())) return false;
		if (time().getHour() != anotherTime.getHour()) return false;
		return true;
	}
	
	/**
	 * Increase total views.
	 * 
	 * @param views the amount of views to be added to current views.
	 */
	public void addViews(Long views) {
		this.views += views;
	}
	
	/**
	 * Increase total sales.
	 * 
	 * @param sales the amount of sales to be added to current sales.
	 */
	public void addSales(Double sales) {
		this.sales += sales;
	}
		
	/**
	 * Increase number of sales (orders).
	 * 
	 * @param order is number of sales (orders) to add to current value.
	 */
	public void addOrder(Long orders) {
		this.orders += orders;
	}
	
	/**
	 * Increase item purchased.
	 * 
	 * @param itemPurchased the number of item purchased to be added to current views.
	 */
	public void addItemPurchased(Long itemPurchased) {
		this.itemPurchased += itemPurchased;
	}
	
	/**
	 * Increase unique visitor.
	 * 
	 * @param uniqueVisitor the number of unique visitor to be added to current unique visitors.
	 */
	public void addUniqueVisitor(Long uniqueVisitor) {
		this.uniqueVisitor += uniqueVisitor;
	}
	
}
