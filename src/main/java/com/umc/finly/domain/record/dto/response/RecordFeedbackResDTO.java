package com.umc.finly.domain.record.dto.response;

import com.umc.finly.domain.record.enums.FeedbackStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// AI 피드백 조회 응답 DTO
// 투자 기록에 대한 AI 감정 분석 결과
@Getter
@Builder
public class RecordFeedbackResDTO {

    private Long feedbackId;         // 피드백 ID
    private Long recordEntryId;      // 연관된 기록 ID
    private FeedbackStatus status;   // 상태 (PENDING, GENERATING, COMPLETED, FAILED)
    private String content;          // AI 피드백 본문 (감정 분석 결과)
    private String suggestion;       // AI 제안 (투자 조언)
    private LocalDateTime createdAt; // 생성 시각
    private LocalDateTime updatedAt; // 수정 시각 (상태 변경 시 갱신)
}
