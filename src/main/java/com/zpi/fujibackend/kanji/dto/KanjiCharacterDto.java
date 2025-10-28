package com.zpi.fujibackend.kanji.dto;

import java.util.UUID;

public record KanjiCharacterDto(
        UUID uuid,
        String character
) {
}
