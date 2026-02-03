package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.RecordFeedbackRes;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.entity.RecordFeedback;

public interface RecordFeedbackService {
    RecordFeedback requestFeedbackAsync(Long memberId, RecordEntry recordEntry);
    RecordFeedbackRes getFeedback(Long memberId, Long recordEntryId);
    RecordFeedbackRes regenerateFeedback(Long memberId, Long recordEntryId);
}
