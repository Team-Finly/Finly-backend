package com.umc.finly.domain.record.service;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.infra.FeedbackPromptBuilder;
import com.umc.finly.domain.record.infra.OpenAiFeedbackClient;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.domain.record.repository.RecordFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// AI 피드백 비동기 생성 실행기
// @Async로 별도 스레드에서 OpenAI API 호출을 처리함
@Slf4j
@Component
@RequiredArgsConstructor
public class RecordFeedbackAsyncExecutor {

    private static final int PAST_RECORDS_LIMIT = 20; // 과거 기록 참조 최대 개수

    private final RecordFeedbackRepository feedbackRepository;
    private final RecordEntryRepository recordEntryRepository;
    private final StockRepository stockRepository;
    private final OpenAiFeedbackClient openAiFeedbackClient; // OpenAI API 클라이언트
    private final FeedbackPromptBuilder promptBuilder; // 프롬프트 생성기

    // 비동기로 AI 피드백 생성
    // REQUIRES_NEW: 호출자 트랜잭션과 별개의 새 트랜잭션에서 실행
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateFeedbackAsync(Long feedbackId, Long memberId, Long recordEntryId) {
        RecordFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElse(null);

        if (feedback == null) {
            log.error("Feedback not found for id: {}", feedbackId);
            return;
        }

        try {
            // 상태를 GENERATING으로 변경
            feedback.markGenerating();
            feedbackRepository.save(feedback);

            // 현재 기록 조회
            RecordEntry currentEntry = recordEntryRepository.findById(recordEntryId)
                    .orElse(null);

            if (currentEntry == null) {
                log.error("RecordEntry not found for id: {}", recordEntryId);
                feedback.markFailed("RecordEntry not found");
                feedbackRepository.save(feedback);
                return;
            }

            // 종목 정보 조회
            Stock stock = stockRepository.findById(currentEntry.getStockId())
                    .orElse(null);

            // 과거 기록 조회 (현재 기록 제외, 최대 20개)
            List<RecordEntry> pastEntries = recordEntryRepository
                    .findByMemberIdOrderByRecordDateDesc(memberId, PageRequest.of(0, PAST_RECORDS_LIMIT));

            pastEntries = pastEntries.stream()
                    .filter(e -> !e.getId().equals(recordEntryId)) // 현재 기록 제외
                    .toList();

            // 프롬프트 생성 및 OpenAI API 호출
            String systemPrompt = promptBuilder.getSystemPrompt();
            String userPrompt = promptBuilder.buildUserPrompt(currentEntry, stock, pastEntries);

            OpenAiFeedbackClient.FeedbackResponse response = openAiFeedbackClient.generateFeedback(systemPrompt, userPrompt);

            // 성공 시 COMPLETED 상태로 변경 + 결과 저장
            feedback.markCompleted(response.content(), response.suggestion(), response.promptTokens(), response.completionTokens());
            feedbackRepository.save(feedback);

            log.info("Feedback generated successfully for recordEntryId: {}", recordEntryId);
        } catch (Exception e) {
            // 실패 시 FAILED 상태로 변경 + 에러 메시지 저장
            log.error("Failed to generate feedback for recordEntryId: {}", recordEntryId, e);
            feedback.markFailed(e.getMessage());
            feedbackRepository.save(feedback);
        }
    }
}
