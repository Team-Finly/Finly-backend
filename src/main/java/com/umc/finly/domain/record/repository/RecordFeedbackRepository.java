package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordFeedback;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecordFeedbackRepository extends JpaRepository<RecordFeedback, Long> {
    Optional<RecordFeedback> findByRecordEntryId(Long recordEntryId);
    Optional<RecordFeedback> findByRecordEntryIdAndMemberId(Long recordEntryId, Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM RecordFeedback f WHERE f.recordEntryId = :recordEntryId")
    Optional<RecordFeedback> findByRecordEntryIdForUpdate(@Param("recordEntryId") Long recordEntryId);
}
