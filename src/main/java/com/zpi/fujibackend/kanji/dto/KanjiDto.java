package com.zpi.fujibackend.kanji.dto;

import java.util.UUID;

public record KanjiDto(
        UUID uuid,
        String character,
        String document,
        String drawingData,
        String svgData
) {
}
