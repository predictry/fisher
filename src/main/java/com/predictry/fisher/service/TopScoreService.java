package com.predictry.fisher.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.predictry.fisher.domain.item.Item;
import com.predictry.fisher.domain.item.TopScore;

@Service
@Transactional
public class TopScoreService {
/*
	@Autowired
	private ItemRepository topScoreRepository;
	
	public TopScore<Long> getTopHits() {
		@SuppressWarnings("unchecked")
		TopScore<Long> topHits = (TopScore<Long>) topScoreRepository.findOne(TopScore.TOP_HIT);
		if (topHits == null) {
			topHits = new TopScore<Long>(TopScore.TOP_HIT);
			topScoreRepository.save(topHits);
		}
		return topHits;
	}
	
	public TopScore<Double> getTopSales() {
		@SuppressWarnings("unchecked")
		TopScore<Double> topSales = (TopScore<Double>) topScoreRepository.findOne(TopScore.TOP_SALES);
		if (topSales == null) {
			topSales = new TopScore<Double>(TopScore.TOP_SALES);
			topScoreRepository.save(topSales);
		}
		return topSales;
	}
	
	public TopScore<Long> addNewTopHits(Item<Long> newScore) {
		TopScore<Long> topHits = getTopHits();
		topHits.addNewScore(newScore);
		return topScoreRepository.save(topHits);
	}
*/	
}
