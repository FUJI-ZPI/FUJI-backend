package com.zpi.fujibackend.vocabulary.dto;


import java.util.List;


public record VocabularyDetailsDto(

        int level,
        String characters,
        List<String> unicodeCharacters,
        WanikaniVocabularyJsonDto details


) {
}
