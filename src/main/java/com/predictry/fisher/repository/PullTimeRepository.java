package com.predictry.fisher.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.predictry.fisher.domain.pull.PullTime;
import org.springframework.stereotype.Repository;

@Repository
public interface PullTimeRepository extends ElasticsearchRepository<PullTime, String> {
	
}
