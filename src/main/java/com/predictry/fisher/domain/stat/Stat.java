package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
	private Long itemPerCart = 0l;
	private Long itemPurchased = 0l;
	private Long orders = 0l;
	private Long uniqueVisitor = 0l;
	
	@JsonIgnore
	private Map<String, Long> cartPerSession = new HashMap<>();
	
	public Stat() {}
	
	public Stat(String time, String tenantId, Long views, Double sales, Long itemPerCart,
			Long itemPurchased, Long orders, Long uniqueVisitor) {
		super();
		this.time = time;
		this.tenantId = tenantId;
		this.views = views;
		this.sales = sales;
		this.itemPerCart = itemPerCart;
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
	
	public Long getItemPerCart() {
		return itemPerCart;
	}
	
	public void setItemPerCart(Long itemPerCart) {
		this.itemPerCart = itemPerCart;
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
	 * Calculate new average item per cart.
	 * 
	 * @param itemPerCart the new item per cart.
	 */
	public void addItemPerCart(Long itemPerCart) {
		if (this.itemPerCart == 0) {
			this.itemPerCart = itemPerCart;
		} else {
			this.itemPerCart = (this.itemPerCart + itemPerCart) / 2;
		}
	}
	
	/**
	 * Add new qty in cart for a session.
	 * 
	 * @param sessionId is a session identifier.
	 * @param qty is number of qty in the cart for an item in that session.
	 */
	public void addItemPerCart(String sessionId, Long qty) {
		if (cartPerSession.containsKey(sessionId)) {
			Long oldQty = cartPerSession.get(sessionId);
			cartPerSession.put(sessionId, oldQty + qty);
		} else {
			cartPerSession.put(sessionId, qty);
		}
	}
	
	/**
	 * Calculate item per cart value based on information added by {@link #addItemPerCart(String, Long)}.
	 * This method will also set the value of <code>itemPerCart</code>.
	 * 
	 * @return the calculated <code>itemPerCart</code>.
	 */
	public Long calculateItemPerCart() {
		Long total = 0l;
		for (Long value: cartPerSession.values()) {
			total += value;
		}
		if (cartPerSession.size() > 0) {
			this.itemPerCart = (long) (total / cartPerSession.size());
		} else {
			this.itemPerCart = 0l;
		}
		return this.itemPerCart;
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
