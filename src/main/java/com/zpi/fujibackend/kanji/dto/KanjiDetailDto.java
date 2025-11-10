package com.zpi.fujibackend.kanji.dto;

import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.domain.Kanji;

import java.util.List;
import java.util.UUID;

public record KanjiDetailDto(
        UUID uuid,
        int level,
        String character,
        String unicodeCharacter,
        WanikaniKanjiJsonDto details,
        List<String> svgPath
) {
    public static KanjiDetailDto toDto(Kanji kanji) {
        return new KanjiDetailDto(
                kanji.getUuid(),
                kanji.getLevel(),
                kanji.getCharacter(),
                kanji.getUnicodeCharacter(),
                JsonConverter.convertToDto(kanji.getDocument(), WanikaniKanjiJsonDto.class),
                JsonConverter.convertJsonStringToListOfString(kanji.getSvgData())

        );
    }
}
