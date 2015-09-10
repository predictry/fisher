package com.predictry.fisher.config;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.Log4jConfigurer;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.predictry.fisher.domain.util.JacksonTimeDeserializer;
import com.predictry.fisher.domain.util.JacksonTimeSerializer;

@Configuration
@EnableElasticsearchRepositories(basePackages="com.predictry.fisher.repository")
@EnableScheduling
@Import({JmsConfig.class})
public class RootConfig {
	
	@Autowired
	Environment env;
	
	@SuppressWarnings("resource")
	@Bean
	public Client elasticsearchClient() {
		Client client;
		if (env.acceptsProfiles("dev")) {
			client = new TransportClient().addTransportAddress(
				new InetSocketTransportAddress("localhost", 9300));	
		} else {
			Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "fisher").build();
			client = new TransportClient(settings).addTransportAddress(
				new InetSocketTransportAddress("localhost", 9500));
			
		}
		return client;
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchTemplate(elasticsearchClient());
	}
	
	@Bean	
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		return new Jackson2ObjectMapperBuilder()
			.modulesToInstall(new JSR310Module())
			.serializerByType(LocalDateTime.class, new JacksonTimeSerializer())
			.deserializerByType(LocalDateTime.class, new JacksonTimeDeserializer())
			.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
		
	/**
	 * Select log4j configuration file based on active Spring's profile
	 */
	@PostConstruct
	public void initLog4j() throws FileNotFoundException {
		if (env.acceptsProfiles("dev", "test")) {
			Log4jConfigurer.initLogging("classpath:log4j-development.xml");
		} else {
			Log4jConfigurer.initLogging("classpath:log4j-production.xml");
		}
	}

}
