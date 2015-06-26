package com.predictry.fisher.domain.overview;


public class StatOverview {

	private Value<Long> pageView;
	private Value<Long> uniqueVisitor;
	private Value<Double> salesAmount;
	private Long orders;
	private Value<Long> itemPurchased;
	
	public StatOverview() {}
	
	public StatOverview(Value<Long> pageView, Value<Long> uniqueVisitor, Value<Double> salesAmount, Long orders, Value<Long> itemPurchased) {
		this.pageView = pageView;
		this.uniqueVisitor = uniqueVisitor;
		this.salesAmount = salesAmount;
		this.orders = orders;
		this.itemPurchased = itemPurchased;
	}

	public Value<Long> getPageView() {
		return pageView;
	}

	public void setPageView(Value<Long> pageView) {
		this.pageView = pageView;
	}

	public Value<Long> getUniqueVisitor() {
		return uniqueVisitor;
	}

	public void setUniqueVisitor(Value<Long> uniqueVisitor) {
		this.uniqueVisitor = uniqueVisitor;
	}

	public Value<Double> getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(Value<Double> salesAmount) {
		this.salesAmount = salesAmount;
	}

	public Long getOrders() {
		return orders;
	}

	public void setOrders(Long orders) {
		this.orders = orders;
	}

	public Value<Long> getItemPurchased() {
		return itemPurchased;
	}

	public void setItemPurchased(Value<Long> itemPurchased) {
		this.itemPurchased = itemPurchased;
	}

	public Double getConversionRate() {
		if ((getPageView() == null) || (getPageView().getOverall() == 0)) {
			return 0.0;
		} else {
			return ((double) getOrders() / getPageView().getOverall());
		}
	}

	/**
	 * Item per cart is calculated as number of item purchased divided by number of individual orders (sales).
	 */
	public Value<Long> getItemPerCart() {
		if ((getOrders() == null) || (getOrders() == 0)) {
			return new Value<Long>(0l, 0l, 0l);
		} else {
			return new Value<Long>((getItemPurchased().getOverall() / getOrders()), 0l, 0l);
		}
	}
	
	/**
	 * Sales per cart is calculated as number of sales amount divided by number of individual orders (sales).
	 */
	public Value<Double> getSalesPerCart() {
		if ((getOrders() == null) || (getOrders() == 0)) {
			return new Value<Double>(0.0, 0.0, 0.0);
		} else {
			return new Value<Double>((getSalesAmount().getOverall() / getOrders()), 0.0, 0.0);
		}
	}
	
}
