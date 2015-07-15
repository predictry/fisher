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
	private Value views = new Value();
	private Value sales = new Value();
	private Value itemPurchased = new Value();
	private Value orders = new Value();
	private Value uniqueVisitor = new Value();
		
	public Stat() {}
	
	public Stat(String time, String tenantId, Double views, Double sales,
			Double itemPurchased, Double orders, Double uniqueVisitor) {
		super();
		this.time = time;
		this.tenantId = tenantId;
		this.views = new Value(views, 0.0, 0.0);
		this.sales = new Value(sales, 0.0, 0.0);
		this.itemPurchased = new Value(itemPurchased, 0.0, 0.0);
		this.orders = new Value(orders, 0.0, 0.0);
		this.uniqueVisitor = new Value(uniqueVisitor, 0.0, 0.0);
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
	
	public Value getViews() {
		return views;
	}
	
	public void setViews(Value views) {
		this.views = views;
	}
	
	public Value getSales() {
		return sales;
	}
	
	public void setSales(Value sales) {
		this.sales = sales;
	}
		
	public Value getItemPurchased() {
		return itemPurchased;
	}
	
	public void setItemPurchased(Value itemPurchased) {
		this.itemPurchased = itemPurchased;
	}
	
	public Value getOrders() {
		return orders;
	}
	
	public void setOrders(Value orders) {
		this.orders = orders;
	}
	
	public Value getUniqueVisitor() {
		return uniqueVisitor;
	}
	
	public void setUniqueVisitor(Value uniqueVisitor) {
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
	public void addViews(Value views) {
		this.views = this.views.plus(views);
	}
	
	/**
	 * Increase total sales.
	 * 
	 * @param sales the amount of sales to be added to current sales.
	 */
	public void addSales(Value sales) {
		this.sales = this.sales.plus(sales);
	}
		
	/**
	 * Increase number of sales (orders).
	 * 
	 * @param order is number of sales (orders) to add to current value.
	 */
	public void addOrder(Value orders) {
		this.orders = this.orders.plus(orders);
	}
	
	/**
	 * Increase item purchased.
	 * 
	 * @param itemPurchased the number of item purchased to be added to current views.
	 */
	public void addItemPurchased(Value itemPurchased) {
		this.itemPurchased = this.itemPurchased.plus(itemPurchased);
	}
	
	/**
	 * Increase unique visitor.
	 * 
	 * @param uniqueVisitor the number of unique visitor to be added to current unique visitors.
	 */
	public void addUniqueVisitor(Value uniqueVisitor) {
		this.uniqueVisitor = this.uniqueVisitor.plus(uniqueVisitor);
	}
	
}
