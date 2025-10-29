package com.zpi.fujibackend.chatbot.controller;


import com.zpi.fujibackend.chatbot.ChatbotFacade;
import com.zpi.fujibackend.chatbot.dto.ChatbotInnerResponseDto;
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
@RequestMapping("/api/v1/chatbot")
class ChatbotController {

    private final ChatbotFacade chatbotFacade;

    private static final class Routes {
        private static final String ASK = "/ask";
    }

    @PostMapping(Routes.ASK)
    ChatbotResponseDto askChatbot(@Valid @RequestBody ChatbotMessageForm request) {
        Optional<ChatbotInnerResponseDto> chatResponseOpt = chatbotFacade.askChatbot(request);
        return chatResponseOpt
                .map(
                        chatResponse -> new ChatbotResponseDto(true, chatResponse)
                )
                .orElseGet(
                        () -> new ChatbotResponseDto(false, null)
                );
    }
}
