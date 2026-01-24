package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordEntryRepository extends JpaRepository<RecordEntry, Long> {
    boolean existsByClientRequestId(String clientRequestId);
}
