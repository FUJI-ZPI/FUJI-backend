package com.zpi.fujibackend.vocabulary.dto;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;


public record VocabularyDetailsDto(

        int level,
        String characters,
        List<String> unicodeCharacters,
        JsonNode details


) {
}
