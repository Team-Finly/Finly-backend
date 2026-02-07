package com.umc.finly.domain.record.dto.res;

import com.umc.finly.domain.record.entity.RecordFeedback;
import com.umc.finly.domain.record.enums.FeedbackStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecordFeedbackResDTO {

    private Long feedbackId;
    private Long recordEntryId;
    private FeedbackStatus status;
    private String content;
    private String suggestion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RecordFeedbackResDTO from(RecordFeedback feedback) {
        return RecordFeedbackResDTO.builder()
                .feedbackId(feedback.getId())
                .recordEntryId(feedback.getRecordEntryId())
                .status(feedback.getStatus())
                .content(feedback.getContent())
                .suggestion(feedback.getSuggestion())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }
}
