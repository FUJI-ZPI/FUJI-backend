package com.zpi.fujibackend.chatbot;

import com.zpi.fujibackend.chatbot.dto.ChatbotInnerResponseDto;
import com.zpi.fujibackend.chatbot.dto.ChatbotMessageForm;

import java.util.Optional;

public interface ChatbotFacade {
    Optional<ChatbotInnerResponseDto> askChatbot(ChatbotMessageForm request);
}
