package com.predictry.fisher.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.predictry.fisher.domain.pull.PullTime;

public interface PullTimeRepository extends ElasticsearchRepository<PullTime, String> {
	
}
