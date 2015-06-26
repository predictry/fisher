package com.predictry.fisher.domain.overview;


public class StatOverview {

	private Value<Long> pageView;
	private Value<Long> uniqueVisitor;
	private Value<Double> salesAmount;
	private Long orders;
	private Value<Long> itemPurchased;
	private Value<Double> salesPerCart;
	private Value<Long> itemPerCart;
	
	public StatOverview() {}
	
	public StatOverview(Value<Long> pageView, Value<Long> uniqueVisitor, Value<Double> salesAmount,
			Long orders, Value<Long> itemPurchased,	Value<Double> salesPerCart, Value<Long> itemPerCart) {
		this.pageView = pageView;
		this.uniqueVisitor = uniqueVisitor;
		this.salesAmount = salesAmount;
		this.orders = orders;
		this.itemPurchased = itemPurchased;
		this.salesPerCart = salesPerCart;
		this.itemPerCart = itemPerCart;
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

	public Value<Double> getSalesPerCart() {
		return salesPerCart;
	}

	public void setSalesPerCart(Value<Double> salesPerCart) {
		this.salesPerCart = salesPerCart;
	}

	public Value<Long> getItemPerCart() {
		return itemPerCart;
	}

	public void setItemPerCart(Value<Long> itemPerCart) {
		this.itemPerCart = itemPerCart;
	}
	
}
