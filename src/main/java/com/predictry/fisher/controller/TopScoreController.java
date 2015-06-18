package com.predictry.fisher.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.ErrorMessage;
import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.TopScore;

@RestController
public class TopScoreController {
//	/**
//	 * Retrieve top 10 hit products.
//	 * 
//	 * @return JSON value (<code>TopHits</code>)
//	 */
//	@RequestMapping("/top/hits")
//	public TopScore<Long> topItems() {
//		TopScore<Long> topHits = new TopScore<>(TopScore.TOP_HIT);
//		topHits.getItems().add(new Item<Long>("Produk1", "Name1", "Url1", 10l));
//		topHits.getItems().add(new Item<Long>("Produk2", "Name2", "Url2", 20l));
//		topHits.getItems().add(new Item<Long>("Produk3", "Name3", "Url3", 30l));
//		topHits.getItems().add(new Item<Long>("Produk4", "Name4", "Url4", 40l));
//		topHits.getItems().add(new Item<Long>("Produk5", "Name5", "Url5", 50l));
//		topHits.getItems().add(new Item<Long>("Produk6", "Name6", "Url6", 60l));
//		topHits.getItems().add(new Item<Long>("Produk7", "Name7", "Url7", 70l));
//		topHits.getItems().add(new Item<Long>("Produk8", "Name8", "Url8", 80l));
//		topHits.getItems().add(new Item<Long>("Produk9", "Name9", "Url9", 90l));
//		topHits.getItems().add(new Item<Long>("Produk10", "Name10", "Url10", 100l));
//		return topHits;
//	}
//	
//	/**
//	 * Retrieve top 10 sales products.
//	 * 
//	 * @return JSON value (<code>TopSales</code>)
//	 */
//	@RequestMapping("/top/sales")
//	public TopScore<Double> topSales() {
//		TopScore<Double> topSales = new TopScore<>(TopScore.TOP_SALES);
//		topSales.getItems().add(new Item<Double>("Produk1", "Name1", "Url1", 10.0));
//		topSales.getItems().add(new Item<Double>("Produk2", "Name2", "Url2", 20.0));
//		topSales.getItems().add(new Item<Double>("Produk3", "Name3", "Url3", 30.0));
//		topSales.getItems().add(new Item<Double>("Produk4", "Name4", "Url4", 40.0));
//		topSales.getItems().add(new Item<Double>("Produk5", "Name5", "Url5", 50.0));
//		topSales.getItems().add(new Item<Double>("Produk6", "Name6", "Url6", 60.0));
//		topSales.getItems().add(new Item<Double>("Produk7", "Name7", "Url7", 70.0));
//		topSales.getItems().add(new Item<Double>("Produk8", "Name8", "Url8", 80.0));
//		topSales.getItems().add(new Item<Double>("Produk9", "Name9", "Url9", 90.0));
//		topSales.getItems().add(new Item<Double>("Produk10", "Name10", "Url10", 100.0));
//		return topSales;
//	}
//
//	/**
//	 * General error handler for this controller.
//	 */
//	@ExceptionHandler(value={Exception.class, RuntimeException.class})
//	public ErrorMessage error(Exception ex) {
//		ErrorMessage error = new ErrorMessage(ex.getMessage());
//		return error;
//	} 

}
