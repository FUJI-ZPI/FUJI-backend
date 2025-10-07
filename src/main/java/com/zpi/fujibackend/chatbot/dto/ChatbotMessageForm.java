package com.zpi.fujibackend.chatbot.dto;


import jakarta.validation.constraints.NotBlank;

public record ChatbotMessageForm(
        @NotBlank
        String userMessage
) {
}