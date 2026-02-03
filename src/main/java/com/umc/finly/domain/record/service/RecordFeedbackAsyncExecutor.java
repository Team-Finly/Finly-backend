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

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordFeedbackAsyncExecutor {

    private static final int PAST_RECORDS_LIMIT = 20;

    private final RecordFeedbackRepository feedbackRepository;
    private final RecordEntryRepository recordEntryRepository;
    private final StockRepository stockRepository;
    private final OpenAiFeedbackClient openAiFeedbackClient;
    private final FeedbackPromptBuilder promptBuilder;

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
            feedback.markGenerating();
            feedbackRepository.save(feedback);

            RecordEntry currentEntry = recordEntryRepository.findById(recordEntryId)
                    .orElse(null);

            if (currentEntry == null) {
                log.error("RecordEntry not found for id: {}", recordEntryId);
                feedback.markFailed("RecordEntry not found");
                feedbackRepository.save(feedback);
                return;
            }

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
}
