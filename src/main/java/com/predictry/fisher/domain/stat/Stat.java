package com.predictry.fisher.domain.stat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.predictry.fisher.domain.TimeBasedEntity;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(indexName="stat")
public class Stat extends TimeBasedEntity {

    @JsonIgnore
    private String tenantId;
	private Value views = new Value();
	private Value sales = new Value();
	private Value itemPurchased = new Value();
	private Value orders = new Value();
	private Value uniqueVisitor = new Value();
	private Value uniqueItemPurchased = new Value();
	private Double cartBoost = 0.0;
	@JsonIgnore
	private Set<String> items = new HashSet<>();
		
	public Stat() {
        super("stat");
    }
	
	public Stat(String time, String tenantId, Double views, Double sales,
			Double itemPurchased, Double orders, Double uniqueVisitor,
			Double uniqueItemPurchased) {
		this(time, tenantId, views, sales, itemPurchased, orders, uniqueVisitor);
		this.uniqueItemPurchased = new Value(uniqueItemPurchased, 0.0, 0.0);
	}
	
	public Stat(String time, String tenantId, Double views, Double sales,
			Double itemPurchased, Double orders, Double uniqueVisitor) {
		super("stat", LocalDateTime.parse(time));
        this.tenantId = tenantId;
		this.views = new Value(views, 0.0, 0.0);
		this.sales = new Value(sales, 0.0, 0.0);
		this.itemPurchased = new Value(itemPurchased, 0.0, 0.0);
		this.orders = new Value(orders, 0.0, 0.0);
		this.uniqueVisitor = new Value(uniqueVisitor, 0.0, 0.0);
	}

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Value getViews() {
		return views;
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

	public Value getOrders() {
		return orders;
	}

	public Value getUniqueVisitor() {
		return uniqueVisitor;
	}

	public Value getUniqueItemPurchased() {
		return uniqueItemPurchased;
	}

	public Double getCartBoost() {
		return this.cartBoost;
	}

	public void setCartBoost(Double cartBoost) {
		this.cartBoost = cartBoost;
	}
	
	public Set<String> getItems() {
		return this.items;
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
	 * @param orders is number of sales (orders) to add to current value.
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
	
	/**
	 * Increase unique item purchased.
	 * 
	 * @param uniqueItemPurchased the number of unique item purchased to current value.
	 */
	public void addUniqueItemPurchased(Value uniqueItemPurchased) {
		this.uniqueItemPurchased = this.uniqueItemPurchased.plus(uniqueItemPurchased);
	}
	
	/**
	 * Add item id processed to this stat.
	 * 
	 * @param itemId is the item unique identifier.
	 */
	public void addItem(String itemId) {
		if (itemId != null) {
			this.items.add(itemId.trim());
		}
	}
	
	/**
	 * Add another stat values to current stat.
	 * 
	 * @param anotherStat an instance of <code>Stat</code> to merge into this instance.
	 */
	public void merge(Stat anotherStat) {
		addViews(anotherStat.getViews());
		addSales(anotherStat.getSales());
		addOrder(anotherStat.getOrders());
		addItemPurchased(anotherStat.getItemPurchased());
		addUniqueVisitor(anotherStat.getUniqueVisitor());
		addUniqueItemPurchased(anotherStat.getUniqueItemPurchased());
		setCartBoost((getCartBoost() + anotherStat.getCartBoost()) / 2); 
	}
	
}
