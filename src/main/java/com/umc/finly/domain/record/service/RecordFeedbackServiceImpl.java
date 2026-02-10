package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.converter.RecordFeedbackConverter;
import com.umc.finly.domain.record.dto.response.RecordFeedbackResDTO;
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

import static com.umc.finly.domain.analysis.association.converter.AnalysisEntryConverter.toResDTO;

// AI 피드백 비즈니스 로직 구현체
// 피드백 생성/조회/재생성과 비동기 실행을 관리함
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordFeedbackServiceImpl implements RecordFeedbackService {

    private final RecordFeedbackConverter recordFeedbackConverter;
    private final RecordFeedbackRepository feedbackRepository;
    private final RecordEntryRepository recordEntryRepository;
    private final RecordFeedbackAsyncExecutor asyncExecutor; // 비동기 실행 담당

    @Override
    @Transactional
    public RecordFeedback requestFeedbackAsync(Long memberId, RecordEntry recordEntry) {
        // PENDING 상태의 피드백 엔티티 생성
        RecordFeedback feedback = RecordFeedback.builder()
                .recordEntryId(recordEntry.getId())
                .memberId(memberId)
                .status(FeedbackStatus.PENDING)
                .build();

        RecordFeedback savedFeedback = feedbackRepository.save(feedback);

        // 트랜잭션 커밋 후 비동기 작업 시작
        // (커밋 전에는 다른 트랜잭션에서 데이터 조회 불가하므로 afterCommit 사용)
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
        // 본인 피드백만 조회 가능
        RecordFeedback feedback = feedbackRepository.findByRecordEntryIdAndMemberId(recordEntryId, memberId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.FEEDBACK_NOT_FOUND));

        return recordFeedbackConverter.toResDTO(feedback);
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

        // 2. 비관적 락으로 피드백 조회 (동시 재생성 요청 경쟁 조건 방지)
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
            // 이미 생성 중이면 에러 (중복 요청 방지)
            if (feedback.getStatus() == FeedbackStatus.PENDING
                    || feedback.getStatus() == FeedbackStatus.GENERATING) {
                throw new CustomException(RecordErrorCode.FEEDBACK_GENERATION_IN_PROGRESS);
            }
            // 기존 피드백 초기화 후 재생성
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

        return recordFeedbackConverter.toResDTO(feedback);
    }
}
