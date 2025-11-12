package com.zpi.fujibackend.radical.dto;

public record RadicalDetailDto(
        int level,
        String character,
        String unicodeCharacter,
        String slug,
        WanikaniRadicalJsonDto details


) {
}
