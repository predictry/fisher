package com.predictry.fisher.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

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
