package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.res.RecordFeedbackResDTO;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;

public interface RecordFeedbackService {
    RecordFeedback requestFeedbackAsync(Long memberId, RecordEntry recordEntry);
    RecordFeedbackResDTO getFeedback(Long memberId, Long recordEntryId);
    RecordFeedbackResDTO regenerateFeedback(Long memberId, Long recordEntryId);
}
