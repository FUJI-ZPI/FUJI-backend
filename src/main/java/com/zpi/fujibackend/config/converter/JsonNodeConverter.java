package com.zpi.fujibackend.config.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class JsonNodeConverter {

    private final ObjectMapper objectMapper;


    public JsonNode toJsonNode(String jsonString) {
        try {
            if (jsonString == null || jsonString.isBlank()) return null;

            return objectMapper.readTree(jsonString);

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON string: " + jsonString, e);
        }
    }

    public <T> T convertToDto(String jsonString, Class<T> targetDtoClass) {
        try {
            if (jsonString == null || jsonString.isBlank()) return null;
            return objectMapper.readValue(jsonString, targetDtoClass);

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON string: " + jsonString, e);
        }

    }


}
