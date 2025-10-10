package com.zpi.fujibackend.chatbot.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatbotMessageForm(

        @NotNull
        List<ChatbotHistorySingleMessage> messages
) {
}