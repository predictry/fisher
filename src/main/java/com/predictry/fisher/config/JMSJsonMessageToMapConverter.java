package com.predictry.fisher.config;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * This is a <code>MappingJackson2MessageConverter</code> that will convert JMS
 * message in form of Json into a <code>Map</code>.
 * 
 * @author jocki
 *
 */
public class JMSJsonMessageToMapConverter extends MappingJackson2MessageConverter {

	private JavaType javaType; 
	
	public JMSJsonMessageToMapConverter() {
		javaType = TypeFactory.defaultInstance().constructType(new TypeReference<Map<String, Object>>() { });
		setTargetType(MessageType.TEXT);
	}
	
	@Override
	protected JavaType getJavaTypeForMessage(Message message) throws JMSException {
		return javaType;
	}
	
}
