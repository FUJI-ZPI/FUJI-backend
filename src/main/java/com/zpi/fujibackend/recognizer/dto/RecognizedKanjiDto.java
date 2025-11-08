package com.zpi.fujibackend.recognizer.dto;

import java.util.UUID;

public record RecognizedKanjiDto(UUID uuid,
                                 String character) {
}
