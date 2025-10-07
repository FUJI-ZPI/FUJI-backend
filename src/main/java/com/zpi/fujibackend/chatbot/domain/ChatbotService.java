package com.zpi.fujibackend.chatbot.domain;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.zpi.fujibackend.chatbot.ChatbotFacade;
import com.zpi.fujibackend.chatbot.dto.ChatbotMessageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
class ChatbotService implements ChatbotFacade {

    private final OpenAIClient client;
    private static final String systemPrompt = """
            You are a friendly and patient Japanese language tutor named "Yuki-sensei". Your goal is to have a simple, everyday conversation in Japanese with a student who is a beginner.
            Follow these rules:
            1.  **Always respond in simple, polite Japanese (です/ます form).**
            2.  Keep your sentences short and easy to understand.
            3.  Ask questions to keep the conversation going.
            4.  If the user makes a small grammatical mistake, don't point it out directly. Instead, subtly correct it by using the correct form in your own response. For example, if the user writes 「私 は 昨日 映画 を 見ます」, you can respond with 「そうですか！面白い 映画 を 見ましたか？」.
            5.  If the user asks for help or an explanation in English, provide a short, simple explanation in English, and then continue the conversation in Japanese.
            """;


    @Override
    public Optional<String> askChatbot(ChatbotMessageForm request) {
        ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_NANO)
                .addSystemMessage(systemPrompt)
                .addUserMessage(request.userMessage())
                .build();

        try {
            ChatCompletion completion = client.chat().completions().create(createParams);
            return completion.choices().getFirst().message().content();
        } catch (Exception e) {
            log.warn("Failed to get response from OpenAI", e);
            return Optional.empty();
        }
    }
}

