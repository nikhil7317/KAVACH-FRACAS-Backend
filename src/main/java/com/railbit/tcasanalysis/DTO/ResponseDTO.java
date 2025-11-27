package com.railbit.tcasanalysis.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.railbit.tcasanalysis.util.NullToEmptyStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Service
@Builder
public class ResponseDTO<T>{

    private T data;
    private String message;
    private Integer status;
    private Integer totalRecords;


    public static <T> ResponseDTO<T> success(T data,Integer totalRecords, String message,
                                             Integer status) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.getSerializerProvider()
                .setNullValueSerializer(new NullToEmptyStringSerializer());

        try {
            String jsonData = objectMapper.writeValueAsString(data);
            data = objectMapper.readValue(jsonData, new TypeReference<T>() {
            });
        } catch (IOException e) {
            // Handle exception
        }

        return ResponseDTO.<T>builder().data(data).totalRecords(totalRecords).message(message)
                .status(status).build();
    }

    public static <T> ResponseDTO<T> error(T data, String message,
                                           Integer status) {
        return ResponseDTO.<T>builder().data(data).message(message)
                .status(status).build();
    }


}
