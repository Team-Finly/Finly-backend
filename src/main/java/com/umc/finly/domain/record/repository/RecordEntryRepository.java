package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecordEntryRepository extends JpaRepository<RecordEntry, Long> {
    boolean existsByClientRequestId(String clientRequestId);
    List<RecordEntry> findByMemberIdOrderByRecordDateDesc(Long memberId, Pageable pageable);
    List<RecordEntry> findByMemberIdAndRecordDateOrderByCreatedAtAsc(Long memberId, LocalDate recordDate);
    List<RecordEntry> findAllByMemberIdAndStockId(Long memberId, Long stockId);
}
