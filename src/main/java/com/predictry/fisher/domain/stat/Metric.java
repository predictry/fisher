package com.predictry.fisher.domain.stat;

public enum Metric {

	VIEWS("views"), SALES_AMOUNT("sales"), ITEM_PER_CART("itemPerCart"), ITEM_PURCHASED("itemPurchased"), 
	UNIQUE_VISITOR("uniqueVisitor"), ORDERS("orders"), UNIQUE_ITEM_PURCHASED("uniqueItemPurchased");
	
	private String keyword;
	
	private Metric(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

}
