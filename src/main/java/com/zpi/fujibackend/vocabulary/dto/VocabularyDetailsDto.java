package com.zpi.fujibackend.vocabulary.dto;

import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;

public record VocabularyDetailsDto(
        int level,
        String characters,
        List<String> unicodeCharacters,
        WanikaniVocabularyJsonDto details,
        List<KanjiDto> componentKanji
) {
}
