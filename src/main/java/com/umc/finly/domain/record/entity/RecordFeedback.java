package com.umc.finly.domain.record.entity;

import com.umc.finly.domain.record.enums.FeedbackStatus;
import com.umc.finly.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

// AI 피드백 엔티티
// 기록에 대한 AI의 감정 분석 및 조언을 저장함
// 상태: PENDING(대기) -> GENERATING(생성중) -> COMPLETED(완료) / FAILED(실패)
@Entity
@Table(name = "record_feedback")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecordFeedback extends CreatedUpdatedDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관된 기록 ID (1:1 관계, 유니크 제약)
    @Column(name = "record_entry_id", nullable = false, unique = true)
    private Long recordEntryId;

    // 피드백 소유자 (권한 검사용)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 피드백 상태 (PENDING, GENERATING, COMPLETED, FAILED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private FeedbackStatus status = FeedbackStatus.PENDING;

    // AI 피드백 본문 (감정 분석 결과)
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // AI 제안 (조언)
    @Column(name = "suggestion", columnDefinition = "TEXT")
    private String suggestion;

    // 실패 시 에러 메시지
    @Column(name = "error_message")
    private String errorMessage;

    // 프롬프트 토큰 수 (비용 추적용)
    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    // 완료 토큰 수 (비용 추적용)
    @Column(name = "completion_tokens")
    private Integer completionTokens;

    // 상태를 GENERATING으로 변경
    public void markGenerating() {
        this.status = FeedbackStatus.GENERATING;
    }

    // 생성 완료 처리 (결과 저장)
    public void markCompleted(String content, String suggestion, Integer promptTokens, Integer completionTokens) {
        this.status = FeedbackStatus.COMPLETED;
        this.content = content;
        this.suggestion = suggestion;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.errorMessage = null;
    }

    // 생성 실패 처리 (에러 메시지 저장)
    public void markFailed(String errorMessage) {
        this.status = FeedbackStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    // 재생성을 위한 초기화
    public void resetForRegeneration() {
        this.status = FeedbackStatus.PENDING;
        this.content = null;
        this.suggestion = null;
        this.errorMessage = null;
        this.promptTokens = null;
        this.completionTokens = null;
    }
}
