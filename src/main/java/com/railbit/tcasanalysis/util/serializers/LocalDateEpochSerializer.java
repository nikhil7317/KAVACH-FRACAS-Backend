package com.railbit.tcasanalysis.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

public class LocalDateEpochSerializer extends com.fasterxml.jackson.databind.JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            // Convert LocalDate to epoch milliseconds
            long epochMillis = value.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();
            gen.writeNumber(epochMillis);
        } else {
            gen.writeNull();
        }
    }
}

