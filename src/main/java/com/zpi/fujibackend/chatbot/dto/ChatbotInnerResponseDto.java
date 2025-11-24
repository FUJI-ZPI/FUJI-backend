package com.zpi.fujibackend.chatbot.dto;

public record ChatbotInnerResponseDto(
        String japanese,
        String english,
        String note
) {
}
