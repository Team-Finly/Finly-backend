package com.umc.finly.domain.record.entity;

import com.umc.finly.domain.record.enums.FeedbackStatus;
import com.umc.finly.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "record_entry_id", nullable = false, unique = true)
    private Long recordEntryId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private FeedbackStatus status = FeedbackStatus.PENDING;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    public void markGenerating() {
        this.status = FeedbackStatus.GENERATING;
    }

    public void markCompleted(String content, Integer promptTokens, Integer completionTokens) {
        this.status = FeedbackStatus.COMPLETED;
        this.content = content;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.errorMessage = null;
    }

    public void markFailed(String errorMessage) {
        this.status = FeedbackStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    public void resetForRegeneration() {
        this.status = FeedbackStatus.PENDING;
        this.content = null;
        this.errorMessage = null;
        this.promptTokens = null;
        this.completionTokens = null;
    }
}
