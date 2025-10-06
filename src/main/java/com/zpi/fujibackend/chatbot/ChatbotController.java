package com.zpi.fujibackend.chatbot;


import com.zpi.fujibackend.chatbot.dto.ChatbotRequestDto;
import com.zpi.fujibackend.chatbot.dto.ChatbotResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotServiceInterface chatbotService;
    private final String errorMessage = "Error, failed to generate response";

    private static final class Routes {
        private static final String ASK = "/ask";
    }

    @PostMapping(Routes.ASK)
    public ResponseEntity<ChatbotResponseDto> askChatbot(@Valid @RequestBody ChatbotRequestDto request) {
        Optional<String> chatResponseOpt = chatbotService.askChatbot(request);
        return chatResponseOpt
                .map(
                        str -> ResponseEntity.ok(new ChatbotResponseDto(true, str))
                )
                .orElseGet(
                        () -> ResponseEntity.ok(new ChatbotResponseDto(false, errorMessage))
                );
    }
}
