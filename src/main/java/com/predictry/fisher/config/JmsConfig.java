package com.predictry.fisher.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@Configuration
@EnableJms
public class JmsConfig {
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JSR310Module());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
	}

	@Bean 
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		ActiveMQConnectionFactory targetFactory = new ActiveMQConnectionFactory();
		targetFactory.setUserName("admin");
		targetFactory.setPassword("admin");
		targetFactory.setBrokerURL("failover:tcp://localhost:61616");
		targetFactory.setUseAsyncSend(true);
		factory.setTargetConnectionFactory(targetFactory);
		return targetFactory;
	}
	
	@Bean(name = "queue")
	public JmsTemplate jmsTemplateQueue() {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
		return jmsTemplate;
	}
	
	@Bean(name = "topic")
	public JmsTemplate jmsTemplateTopic() {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
		jmsTemplate.setPubSubDomain(true);
		return jmsTemplate;
	}
	
	@Bean
	public DefaultJmsListenerContainerFactory queueJmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setSessionTransacted(true);
		factory.setConcurrency("3-10");
		factory.setMessageConverter(new JMSJsonMessageToMapConverter());
		return factory;
	}
	
	@Bean
	public DefaultJmsListenerContainerFactory topicJmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setPubSubDomain(true);
		factory.setConnectionFactory(connectionFactory());
		factory.setSessionTransacted(true);
		factory.setMessageConverter(new JMSJsonMessageToMapConverter());
		return factory;
	}

}
