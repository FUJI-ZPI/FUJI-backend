package com.zpi.fujibackend.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

enum MessageType {
    USER,
    BOT
}

public record ChatbotHistorySingleMessage(

        @NotNull
        MessageType messageType,

        @NotBlank
        @Size(min = 1, max = 300)
        String message,

        @NotNull
        LocalDateTime dateTime

) {
    public String toConversationString() {
        String speaker = messageType == MessageType.USER ? "User: " : "Yuki-sensei: ";
        String time = " (" + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")";
        return speaker + message + time;

    }
}
