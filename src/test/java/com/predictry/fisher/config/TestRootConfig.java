package com.predictry.fisher.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@Configuration
@ComponentScan(basePackages={"com.predictry.fisher.service", "com.predictry.fisher.repository"})
@EnableElasticsearchRepositories(basePackages="com.predictry.fisher.repository")
public class TestRootConfig {
	
	@Autowired
	Environment env;
	
	@SuppressWarnings("resource")
	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		Client client;
		if (env.acceptsProfiles("dev")) {
			client = new TransportClient().addTransportAddress(
				new InetSocketTransportAddress("localhost", 9300));	
		} else {
			client = new TransportClient().addTransportAddress(
				new InetSocketTransportAddress("localhost", 9300));
		}
		return new ElasticsearchTemplate(client);
	}
	
	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		return new Jackson2ObjectMapperBuilder()
			.modulesToInstall(new JSR310Module())
			.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

}