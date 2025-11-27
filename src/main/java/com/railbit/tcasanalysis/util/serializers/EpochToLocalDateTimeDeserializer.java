package com.railbit.tcasanalysis.util.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class EpochToLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final Logger log = LogManager.getLogger(EpochToLocalDateTimeDeserializer.class);

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        long epochMillis = p.getLongValue();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.of("Asia/Kolkata"));
    }
}
