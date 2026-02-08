package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.res.RecordFeedbackResDTO;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.enums.FeedbackStatus;
import com.umc.finly.domain.record.exception.code.RecordErrorCode;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import com.umc.finly.domain.record.repository.RecordFeedbackRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordFeedbackServiceImpl implements RecordFeedbackService {

    private final RecordFeedbackRepository feedbackRepository;
    private final RecordEntryRepository recordEntryRepository;
    private final RecordFeedbackAsyncExecutor asyncExecutor;

    @Override
    @Transactional
    public RecordFeedback requestFeedbackAsync(Long memberId, RecordEntry recordEntry) {
        RecordFeedback feedback = RecordFeedback.builder()
                .recordEntryId(recordEntry.getId())
                .memberId(memberId)
                .status(FeedbackStatus.PENDING)
                .build();

        RecordFeedback savedFeedback = feedbackRepository.save(feedback);

        // 트랜잭션 커밋 후 비동기 작업 시작 (커밋 전에는 다른 트랜잭션에서 데이터 조회 불가)
        Long feedbackId = savedFeedback.getId();
        Long recordEntryId = recordEntry.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                asyncExecutor.generateFeedbackAsync(feedbackId, memberId, recordEntryId);
            }
        });

        return savedFeedback;
    }

    @Override
    @Transactional(readOnly = true)
    public RecordFeedbackResDTO getFeedback(Long memberId, Long recordEntryId) {
        RecordFeedback feedback = feedbackRepository.findByRecordEntryIdAndMemberId(recordEntryId, memberId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.FEEDBACK_NOT_FOUND));

        return RecordFeedbackResDTO.from(feedback);
    }

    @Override
    @Transactional
    public RecordFeedbackResDTO regenerateFeedback(Long memberId, Long recordEntryId) {
        // 1. 기록이 존재하고 본인 것인지 확인
        RecordEntry recordEntry = recordEntryRepository.findById(recordEntryId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.RECORD_NOT_FOUND));

        if (!recordEntry.getMemberId().equals(memberId)) {
            throw new CustomException(RecordErrorCode.RECORD_FORBIDDEN);
        }

        // 2. 피드백 조회 (행 잠금으로 동시 재생성 요청 경쟁 조건 방지)
        RecordFeedback feedback = feedbackRepository.findByRecordEntryIdForUpdate(recordEntryId)
                .orElse(null);

        if (feedback == null) {
            // 피드백이 없으면 새로 생성 (유니크 제약으로 동시 중복 생성 방지)
            try {
                feedback = RecordFeedback.builder()
                        .recordEntryId(recordEntryId)
                        .memberId(memberId)
                        .status(FeedbackStatus.PENDING)
                        .build();
                feedback = feedbackRepository.save(feedback);
            } catch (DataIntegrityViolationException e) {
                throw new CustomException(RecordErrorCode.FEEDBACK_GENERATION_IN_PROGRESS);
            }
        } else {
            // 피드백이 대기 중이거나 생성 중이면 에러
            if (feedback.getStatus() == FeedbackStatus.PENDING
                    || feedback.getStatus() == FeedbackStatus.GENERATING) {
                throw new CustomException(RecordErrorCode.FEEDBACK_GENERATION_IN_PROGRESS);
            }
            feedback.resetForRegeneration();
            feedback = feedbackRepository.save(feedback);
        }

        // 트랜잭션 커밋 후 비동기 작업 시작
        Long feedbackId = feedback.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                asyncExecutor.generateFeedbackAsync(feedbackId, memberId, recordEntryId);
            }
        });

        return RecordFeedbackResDTO.from(feedback);
    }
}
