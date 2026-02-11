package com.umc.finly.domain.record.converter;

import com.umc.finly.domain.record.dto.response.RecordFeedbackResDTO;
import com.umc.finly.domain.record.entity.RecordFeedback;

import org.springframework.stereotype.Component;

@Component
public class RecordFeedbackConverter {

    // RecordFeedback 엔티티를 응답 DTO로 변환
    public RecordFeedbackResDTO toResDTO(RecordFeedback feedback) {
        return RecordFeedbackResDTO.from(feedback);
    }
}
