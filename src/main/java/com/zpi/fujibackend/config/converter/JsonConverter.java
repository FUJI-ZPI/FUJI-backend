package com.zpi.fujibackend.config.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class JsonConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T convertToDto(String jsonString, Class<T> targetDtoClass) {
        try {
            if (jsonString == null || jsonString.isBlank()) return null;
            return OBJECT_MAPPER.readValue(jsonString, targetDtoClass);

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON string: " + jsonString, e);
        }
    }

    public static List<String> convertJsonStringToListOfString(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return OBJECT_MAPPER.readValue(jsonString, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON string: " + jsonString, e);
        }
    }
}
