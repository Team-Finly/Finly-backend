package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.res.RecordFeedbackResDTO;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;

// AI 피드백 관련 비즈니스 로직 인터페이스
// 피드백 생성 요청, 조회, 재생성을 담당함
// 구현체: RecordFeedbackServiceImpl
public interface RecordFeedbackService {

    // AI 피드백 비동기 생성 요청
    // 트랜잭션 커밋 후 비동기로 OpenAI API 호출됨
    RecordFeedback requestFeedbackAsync(Long memberId, RecordEntry recordEntry);

    // 피드백 조회 (상태: PENDING/GENERATING/COMPLETED/FAILED)
    RecordFeedbackResDTO getFeedback(Long memberId, Long recordEntryId);

    // 피드백 재생성 요청 (기존 피드백 초기화 후 다시 생성)
    RecordFeedbackResDTO regenerateFeedback(Long memberId, Long recordEntryId);
}
