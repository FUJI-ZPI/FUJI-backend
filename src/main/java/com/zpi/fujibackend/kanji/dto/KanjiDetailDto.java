package com.zpi.fujibackend.kanji.dto;

import com.zpi.fujibackend.algorithm.KanjiNormalizer;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record KanjiDetailDto(
        UUID uuid,
        int level,
        String character,
        String unicodeCharacter,
        WanikaniKanjiJsonDto details,
        List<String> svgPath,
        List<RadicalDto> componentRadicals,
        List<VocabularyDto> relatedVocabulary,
        List<KanjiDto> visuallySimilarKanji,
        List<List<List<Double>>> referenceStrokes
) {
    public static KanjiDetailDto toDto(Kanji kanji) {
        List<List<List<Double>>> normalizedStrokes =
                KanjiNormalizer.momentNormalize(kanji.getDrawingData());
        return new KanjiDetailDto(
                kanji.getUuid(),
                kanji.getLevel(),
                kanji.getCharacter(),
                kanji.getUnicodeCharacter(),
                JsonConverter.convertToDto(kanji.getDocument(), WanikaniKanjiJsonDto.class),
                JsonConverter.convertJsonStringToListOfString(kanji.getSvgData()),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                normalizedStrokes
        );
    }
}
