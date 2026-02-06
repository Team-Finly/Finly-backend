package com.umc.finly.domain.home.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HomeMindRepository extends JpaRepository<RecordEntry, Long> {

    @Query("""
        select distinct r.recordDate
        from RecordEntry r
        where r.memberId = :memberId
          and r.recordDate between :start and :end
    """)
    List<LocalDate> findRecordedDatesInPeriod(
            Long memberId,
            LocalDate start,
            LocalDate end
    );
}
