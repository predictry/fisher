package com.predictry.fisher.domain.stat;

public class StatOverview {

	private Value pageView;
	private Value uniqueVisitor;
	private Value salesAmount;
	private Value orders;
	private Value itemPurchased;
	
	public StatOverview() {}
	
	public StatOverview(Double pageView, Double uniqueVisitor, Double salesAmount, Double orders, Double itemPurchased) {
		this.pageView = new Value(pageView, 0.0, 0.0);
		this.uniqueVisitor = new Value(uniqueVisitor, 0.0, 0.0);
		this.salesAmount = new Value(salesAmount, 0.0, 0.0);
		this.orders = new Value(orders, 0.0, 0.0);
		this.itemPurchased = new Value(itemPurchased, 0.0, 0.0);
	}

	public Value getPageView() {
		return pageView;
	}

	public void setPageView(Value pageView) {
		this.pageView = pageView;
	}

	public Value getUniqueVisitor() {
		return uniqueVisitor;
	}

	public void setUniqueVisitor(Value uniqueVisitor) {
		this.uniqueVisitor = uniqueVisitor;
	}

	public Value getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(Value salesAmount) {
		this.salesAmount = salesAmount;
	}

	public Value getOrders() {
		return orders;
	}

	public void setOrders(Value orders) {
		this.orders = orders;
	}

	public Value getItemPurchased() {
		return itemPurchased;
	}

	public void setItemPurchased(Value itemPurchased) {
		this.itemPurchased = itemPurchased;
	}

	public Value getConversionRate() {
		return getOrders().divide(getPageView());
	}

	/**
	 * Item per cart is calculated as number of item purchased divided by number of individual orders (sales).
	 */
	public Value getItemPerCart() {
		return getItemPurchased().divide(getOrders());
	}
	
	/**
	 * Sales per cart is calculated as number of sales amount divided by number of individual orders (sales).
	 */
	public Value getSalesPerCart() {
		return getSalesAmount().divide(getOrders());
	}
	
}
