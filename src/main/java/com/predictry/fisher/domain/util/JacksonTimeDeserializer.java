package com.predictry.fisher.domain.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JacksonTimeDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		String value = parser.getValueAsString();
		return value.equals("null")? null: LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

}
