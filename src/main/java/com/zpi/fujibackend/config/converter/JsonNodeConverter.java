package com.zpi.fujibackend.config.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public class JsonNodeConverter {
    public static <T> T convertToDto(String jsonString, Class<T> targetDtoClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (jsonString == null || jsonString.isBlank()) return null;
            return objectMapper.readValue(jsonString, targetDtoClass);

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON string: " + jsonString, e);
        }

    }
}
