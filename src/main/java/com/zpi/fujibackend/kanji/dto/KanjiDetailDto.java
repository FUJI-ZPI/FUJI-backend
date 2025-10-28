package com.zpi.fujibackend.kanji.dto;

import java.util.UUID;

public record KanjiDetailDto(
        UUID uuid,
        int level,
        String character,
        String unicodeCharacter,
        WanikaniKanjiJsonDto details

) {
}
