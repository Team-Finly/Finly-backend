package com.umc.finly.domain.record.service;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.record.dto.RecordFeedbackRes;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.enums.FeedbackStatus;
import com.umc.finly.domain.record.infra.FeedbackPromptBuilder;
import com.umc.finly.domain.record.infra.OpenAiFeedbackClient;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.domain.record.repository.RecordFeedbackRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordFeedbackServiceImpl implements RecordFeedbackService {

    private static final int PAST_RECORDS_LIMIT = 20;

    private final RecordFeedbackRepository feedbackRepository;
    private final RecordEntryRepository recordEntryRepository;
    private final StockRepository stockRepository;
    private final OpenAiFeedbackClient openAiFeedbackClient;
    private final FeedbackPromptBuilder promptBuilder;

    @Override
    @Transactional
    public RecordFeedback requestFeedbackAsync(Long memberId, RecordEntry recordEntry) {
        RecordFeedback feedback = RecordFeedback.builder()
                .recordEntryId(recordEntry.getId())
                .memberId(memberId)
                .status(FeedbackStatus.PENDING)
                .build();

        RecordFeedback savedFeedback = feedbackRepository.save(feedback);

        generateFeedbackAsync(savedFeedback.getId(), memberId, recordEntry.getId());

        return savedFeedback;
    }

    @Async
    @Transactional
    public void generateFeedbackAsync(Long feedbackId, Long memberId, Long recordEntryId) {
        RecordFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElse(null);

        if (feedback == null) {
            log.error("Feedback not found for id: {}", feedbackId);
            return;
        }

        try {
            feedback.markGenerating();
            feedbackRepository.save(feedback);

            RecordEntry currentEntry = recordEntryRepository.findById(recordEntryId)
                    .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

            Stock stock = stockRepository.findById(currentEntry.getStockId())
                    .orElse(null);

            List<RecordEntry> pastEntries = recordEntryRepository
                    .findByMemberIdOrderByRecordDateDesc(memberId, PageRequest.of(0, PAST_RECORDS_LIMIT));

            pastEntries = pastEntries.stream()
                    .filter(e -> !e.getId().equals(recordEntryId))
                    .toList();

            String systemPrompt = promptBuilder.getSystemPrompt();
            String userPrompt = promptBuilder.buildUserPrompt(currentEntry, stock, pastEntries);

            OpenAiFeedbackClient.FeedbackResponse response = openAiFeedbackClient.generateFeedback(systemPrompt, userPrompt);

            feedback.markCompleted(response.content(), response.promptTokens(), response.completionTokens());
            feedbackRepository.save(feedback);

            log.info("Feedback generated successfully for recordEntryId: {}", recordEntryId);
        } catch (Exception e) {
            log.error("Failed to generate feedback for recordEntryId: {}", recordEntryId, e);
            feedback.markFailed(e.getMessage());
            feedbackRepository.save(feedback);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RecordFeedbackRes getFeedback(Long memberId, Long recordEntryId) {
        RecordFeedback feedback = feedbackRepository.findByRecordEntryIdAndMemberId(recordEntryId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        return RecordFeedbackRes.from(feedback);
    }

    @Override
    @Transactional
    public RecordFeedbackRes regenerateFeedback(Long memberId, Long recordEntryId) {
        // 1. 기록이 존재하고 본인 것인지 확인
        RecordEntry recordEntry = recordEntryRepository.findById(recordEntryId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        if (!recordEntry.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.RECORD_FORBIDDEN);
        }

        // 2. 피드백 조회 또는 새로 생성
        RecordFeedback feedback = feedbackRepository.findByRecordEntryId(recordEntryId)
                .orElse(null);

        if (feedback == null) {
            // 피드백이 없으면 새로 생성
            feedback = RecordFeedback.builder()
                    .recordEntryId(recordEntryId)
                    .memberId(memberId)
                    .status(FeedbackStatus.PENDING)
                    .build();
            feedback = feedbackRepository.save(feedback);
        } else {
            // 피드백이 이미 생성 중이면 에러
            if (feedback.getStatus() == FeedbackStatus.GENERATING) {
                throw new CustomException(ErrorCode.FEEDBACK_GENERATION_IN_PROGRESS);
            }
            feedback.resetForRegeneration();
            feedback = feedbackRepository.save(feedback);
        }

        generateFeedbackAsync(feedback.getId(), memberId, recordEntryId);

        return RecordFeedbackRes.from(feedback);
    }
}
