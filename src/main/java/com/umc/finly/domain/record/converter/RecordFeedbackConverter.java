package com.umc.finly.domain.record.converter;

import com.umc.finly.domain.record.dto.response.RecordFeedbackResDTO;
import com.umc.finly.domain.record.entity.RecordFeedback;

import org.springframework.stereotype.Component;

@Component
public class RecordFeedbackConverter {

    // RecordFeedback 엔티티를 응답 DTO로 변환
    public RecordFeedbackResDTO toResDTO(RecordFeedback feedback) {
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
