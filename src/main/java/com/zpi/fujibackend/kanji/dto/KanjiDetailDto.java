package com.zpi.fujibackend.kanji.dto;

import com.zpi.fujibackend.config.converter.JsonNodeConverter;
import com.zpi.fujibackend.kanji.domain.Kanji;

import java.util.UUID;

public record KanjiDetailDto(
        UUID uuid,
        int level,
        String character,
        String unicodeCharacter,
        WanikaniKanjiJsonDto details
) {
    public static KanjiDetailDto toDto(Kanji kanji) {
        return new KanjiDetailDto(
                kanji.getUuid(),
                kanji.getLevel(),
                kanji.getCharacter(),
                kanji.getUnicodeCharacter(),
                JsonNodeConverter.convertToDto(kanji.getDocument(), WanikaniKanjiJsonDto.class)
        );
    }
}
