package com.zpi.fujibackend.chatbot.dto;

import javax.annotation.Nullable;

public record ChatbotResponseDto(
        boolean success,
        @Nullable
        ChatbotInnerResponseDto response
) {
}