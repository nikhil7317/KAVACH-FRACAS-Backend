package com.railbit.tcasanalysis.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class NullToEmptyStringSerializer extends JsonSerializer<Object> {
	@Override
	public void serialize(Object value, JsonGenerator gen,
			SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeString("");
		} else {
			gen.writeObject(value);
		}
	}
}