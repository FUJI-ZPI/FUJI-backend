package com.zpi.fujibackend.kanji.dto;

import java.util.List;
import java.util.UUID;

public record ReferenceKanjiDto(UUID uuid,
                                String character,
                                List<List<List<Double>>> drawingData) {
}
