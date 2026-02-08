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

// OpenAI API 클라이언트
// Chat Completions API를 호출하여 AI 피드백 생성
// JSON 응답 파싱 및 에러 처리 담당
@Slf4j
@Component
public class OpenAiFeedbackClient {

    private final RestClient openAiRestClient; // OpenAI API 호출용 RestClient
    private final ObjectMapper objectMapper;   // JSON 파싱용

    public OpenAiFeedbackClient(@Qualifier("openAiRestClient") RestClient openAiRestClient,
                                 ObjectMapper objectMapper) {
        this.openAiRestClient = openAiRestClient;
        this.objectMapper = objectMapper;
    }

    // 사용할 OpenAI 모델 (기본: gpt-4o-mini)
    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    // AI 피드백 생성 (메인 메서드)
    // systemPrompt: AI 역할 정의, userPrompt: 기록 데이터
    // 반환: content, suggestion, 토큰 사용량
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

    // OpenAI 응답에서 JSON 파싱하여 FeedbackResponse 생성
    // 파싱 실패 시 정규식 fallback 시도
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

    // JSON 파싱 실패 시 정규식으로 content 추출 시도
    // AI가 가끔 불완전한 JSON을 반환하는 경우 대비
    private FeedbackResponse extractFallbackContent(String rawContent, Integer promptTokens, Integer completionTokens) {
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

    // 마크다운 코드블록 제거 (```json ... ```)
    // AI가 JSON을 코드블록으로 감싸서 반환하는 경우 처리
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

    // ===== 내부 DTO (Record) =====

    // AI 응답 JSON 파싱용
    private record FeedbackJsonResponse(
            String content,    // 피드백 본문
            String suggestion  // 행동 제안
    ) {}

    // 피드백 생성 결과 (외부 반환용)
    public record FeedbackResponse(
            String content,           // 피드백 본문
            String suggestion,        // 행동 제안
            Integer promptTokens,     // 프롬프트 토큰 수
            Integer completionTokens  // 응답 토큰 수
    ) {}

    // OpenAI Chat API 요청 DTO
    private record ChatRequest(
            String model,                              // 모델명 (gpt-4o-mini 등)
            List<Message> messages,                    // 메시지 리스트 (system + user)
            double temperature,                        // 창의성 (0.0~2.0, 낮을수록 일관성)
            @JsonProperty("max_tokens") int maxTokens  // 최대 응답 토큰
    ) {}

    // 메시지 DTO (role: system/user/assistant)
    private record Message(
            String role,    // 역할
            String content  // 내용
    ) {}

    // OpenAI Chat API 응답 DTO
    private record ChatResponse(
            List<Choice> choices,  // 응답 목록 (보통 1개)
            Usage usage            // 토큰 사용량
    ) {}

    // 응답 선택지
    private record Choice(
            Message message  // AI 응답 메시지
    ) {}

    // 토큰 사용량
    private record Usage(
            @JsonProperty("prompt_tokens") Integer promptTokens,        // 프롬프트 토큰
            @JsonProperty("completion_tokens") Integer completionTokens // 응답 토큰
    ) {}
}
