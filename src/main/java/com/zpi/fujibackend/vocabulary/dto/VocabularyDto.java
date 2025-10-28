package com.zpi.fujibackend.vocabulary.dto;

import java.util.UUID;

public record VocabularyDto(
        UUID uuid,
        String characters
) {
}
