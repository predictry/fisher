package com.predictry.fisher.domain.util;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessageCreator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMessageCreator implements MessageCreator {
	
	private static final Logger log = LoggerFactory.getLogger(JsonMessageCreator.class);
	
	private Object sourceObject;
	private ObjectMapper mapper;
	
	public JsonMessageCreator(Object sourceObject, ObjectMapper mapper) {
		this.sourceObject = sourceObject;
		this.mapper = mapper;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		try {
			return session.createTextMessage(mapper.writeValueAsString(sourceObject));
		} catch (JsonProcessingException e) {
			log.error("Error while sending Json to FISHER.ADD_ITEM", e);
		}
		return null;
	}

}
