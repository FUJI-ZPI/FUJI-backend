package com.zpi.fujibackend.chatbot;

import com.zpi.fujibackend.chatbot.dto.ChatbotRequestDto;

import java.util.Optional;

public interface ChatbotServiceInterface {
    Optional<String> askChatbot(ChatbotRequestDto request);
}
