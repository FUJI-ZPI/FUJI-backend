package com.zpi.fujibackend.radical.dto;

import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;

public record RadicalDetailDto(
        int level,
        String character,
        String unicodeCharacter,
        String slug,
        WanikaniRadicalJsonDto details,
        List<KanjiDto> kanjiDto
) {
}
