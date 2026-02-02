package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordFeedbackRepository extends JpaRepository<RecordFeedback, Long> {
    Optional<RecordFeedback> findByRecordEntryId(Long recordEntryId);
    Optional<RecordFeedback> findByRecordEntryIdAndMemberId(Long recordEntryId, Long memberId);
}
