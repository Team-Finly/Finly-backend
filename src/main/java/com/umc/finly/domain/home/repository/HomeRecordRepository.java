package com.umc.finly.domain.home.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HomeRecordRepository extends JpaRepository<RecordEntry, Long> {

    List<RecordEntry> findByMemberIdAndRecordDateBetween(
            Long memberId,
            LocalDate start,
            LocalDate end
    );
}
