package com.zpi.fujibackend.chatbot;

import com.zpi.fujibackend.chatbot.dto.ChatbotMessageForm;

import java.util.Optional;

public interface ChatbotFacade {
    Optional<String> askChatbot(ChatbotMessageForm request);
}
