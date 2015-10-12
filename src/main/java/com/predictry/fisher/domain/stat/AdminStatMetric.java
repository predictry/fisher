package com.predictry.fisher.domain.stat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;

public class AdminStatMetric {

	@JsonSerialize(using=JacksonTimeSerializer.class)
	@JsonDeserialize(using=JacksonTimeDeserializer.class)
	private LocalDateTime time;
	
	@JsonProperty("unique_visitor")
	private Long uniqueVisitor;
	
	private Double sales;
	
	private Long skus;
	
	@JsonProperty("page_view")
	private Long pageView;
	
	@JsonProperty("recommended_view")
	private Long recommendedPageView;

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Long getUniqueVisitor() {
		return uniqueVisitor;
	}

	public void setUniqueVisitor(Long uniqueVisitor) {
		this.uniqueVisitor = uniqueVisitor;
	}

	public Double getSales() {
		return sales;
	}

	public void setSales(Double sales) {
		this.sales = sales;
	}

	public Long getSkus() {
		return skus;
	}

	public void setSkus(Long skus) {
		this.skus = skus;
	}

	public Long getPageView() {
		return pageView;
	}

	public void setPageView(Long pageView) {
		this.pageView = pageView;
	}

	public Long getRecommendedPageView() {
		return recommendedPageView;
	}

	public void setRecommendedPageView(Long recommendedPageView) {
		this.recommendedPageView = recommendedPageView;
	}
		
}
