package com.zpi.fujibackend.chatbot.dto;

public record ChatbotResponseDto(
        boolean success,
        String response
) {
}