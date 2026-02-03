package com.umc.finly.domain.record.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class OpenAiFeedbackClient {

    private final RestClient openAiRestClient;

    public OpenAiFeedbackClient(@Qualifier("openAiRestClient") RestClient openAiRestClient) {
        this.openAiRestClient = openAiRestClient;
    }

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    public FeedbackResponse generateFeedback(String systemPrompt, String userPrompt) {
        ChatRequest request = new ChatRequest(
                model,
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", userPrompt)
                ),
                0.7,
                1000
        );

        try {
            ChatResponse response = openAiRestClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(ChatResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new CustomException(ErrorCode.OPENAI_API_FAILED);
            }

            String content = response.choices().get(0).message().content();
            Integer promptTokens = response.usage() != null ? response.usage().promptTokens() : null;
            Integer completionTokens = response.usage() != null ? response.usage().completionTokens() : null;

            return new FeedbackResponse(content, promptTokens, completionTokens);
        } catch (RestClientException e) {
            log.error("OpenAI API call failed", e);
            throw new CustomException(ErrorCode.OPENAI_API_FAILED);
        }
    }

    public record FeedbackResponse(
            String content,
            Integer promptTokens,
            Integer completionTokens
    ) {}

    private record ChatRequest(
            String model,
            List<Message> messages,
            double temperature,
            @JsonProperty("max_tokens") int maxTokens
    ) {}

    private record Message(
            String role,
            String content
    ) {}

    private record ChatResponse(
            List<Choice> choices,
            Usage usage
    ) {}

    private record Choice(
            Message message
    ) {}

    private record Usage(
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("completion_tokens") Integer completionTokens
    ) {}
}
