package com.railbit.tcasanalysis.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeToEpochSerializer extends JsonSerializer<LocalDateTime> {
    private static final Logger log = LogManager.getLogger(LocalDateTimeToEpochSerializer.class);

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        long epochMillis = value.atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();
        gen.writeNumber(epochMillis);
    }
}

