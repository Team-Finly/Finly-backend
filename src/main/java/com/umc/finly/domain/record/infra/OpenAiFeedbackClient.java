package com.umc.finly.domain.record.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.finly.domain.record.exception.code.RecordErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;
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
    private final ObjectMapper objectMapper;

    public OpenAiFeedbackClient(@Qualifier("openAiRestClient") RestClient openAiRestClient,
                                 ObjectMapper objectMapper) {
        this.openAiRestClient = openAiRestClient;
        this.objectMapper = objectMapper;
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
                1500
        );

        try {
            ChatResponse response = openAiRestClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(ChatResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new CustomException(RecordErrorCode.OPENAI_API_FAILED);
            }

            String rawContent = response.choices().get(0).message().content();
            Integer promptTokens = response.usage() != null ? response.usage().promptTokens() : null;
            Integer completionTokens = response.usage() != null ? response.usage().completionTokens() : null;

            return parseFeedbackJson(rawContent, promptTokens, completionTokens);
        } catch (RestClientException e) {
            log.error("OpenAI API call failed", e);
            throw new CustomException(RecordErrorCode.OPENAI_API_FAILED);
        }
    }

    private FeedbackResponse parseFeedbackJson(String rawContent, Integer promptTokens, Integer completionTokens) {
        try {
            String jsonContent = extractJson(rawContent);
            FeedbackJsonResponse parsed = objectMapper.readValue(jsonContent, FeedbackJsonResponse.class);

            return new FeedbackResponse(parsed.content(), parsed.suggestion(), promptTokens, completionTokens);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse feedback JSON: {}", e.getMessage());
            return extractFallbackContent(rawContent, promptTokens, completionTokens);
        }
    }

    private FeedbackResponse extractFallbackContent(String rawContent, Integer promptTokens, Integer completionTokens) {
        // JSON 파싱 실패 시 정규식으로 content 필드 추출 시도
        java.util.regex.Pattern contentPattern = java.util.regex.Pattern.compile(
                "\"content\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"",
                java.util.regex.Pattern.DOTALL
        );
        java.util.regex.Matcher matcher = contentPattern.matcher(rawContent);

        if (matcher.find()) {
            String extractedContent = matcher.group(1)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
            log.info("Extracted content from raw response using regex fallback");
            return new FeedbackResponse(extractedContent, null, promptTokens, completionTokens);
        }

        // 정규식 추출도 실패하면 에러 처리
        log.error("Failed to extract content from OpenAI response");
        throw new CustomException(RecordErrorCode.OPENAI_API_FAILED);
    }

    private String extractJson(String rawContent) {
        String trimmed = rawContent.trim();
        if (trimmed.toLowerCase().startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    private record FeedbackJsonResponse(
            String content,
            String suggestion
    ) {}

    public record FeedbackResponse(
            String content,
            String suggestion,
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
