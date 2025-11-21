package com.zpi.fujibackend.srs.dto;

import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;

import java.util.UUID;

public record CardDto(UUID uuid,
                      KanjiDetailDto kanji) {
}
