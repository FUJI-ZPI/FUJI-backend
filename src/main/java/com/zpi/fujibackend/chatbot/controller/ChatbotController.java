package com.zpi.fujibackend.chatbot.controller;


import com.zpi.fujibackend.chatbot.ChatbotFacade;
import com.zpi.fujibackend.chatbot.dto.ChatbotMessageForm;
import com.zpi.fujibackend.chatbot.dto.ChatbotResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chatbot/v1")
class ChatbotController {

    private final ChatbotFacade chatbotService;
    private static final String errorMessage = "Error, failed to generate response";

    private static final class Routes {
        private static final String ASK = "/ask";
    }

    @PostMapping(Routes.ASK)
    ChatbotResponseDto askChatbot(@Valid @RequestBody ChatbotMessageForm request) {
        Optional<String> chatResponseOpt = chatbotService.askChatbot(request);
        return chatResponseOpt
                .map(
                        str -> new ChatbotResponseDto(true, str)
                )
                .orElseGet(
                        () -> new ChatbotResponseDto(false, errorMessage)
                );
    }
}
