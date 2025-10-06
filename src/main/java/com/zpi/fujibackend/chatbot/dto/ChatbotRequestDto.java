package com.zpi.fujibackend.chatbot.dto;


import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

public record ChatbotRequestDto(
        @NotNull
        @NotBlank
        String userMessage
) {
}