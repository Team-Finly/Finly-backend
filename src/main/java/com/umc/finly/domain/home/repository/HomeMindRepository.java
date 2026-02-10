package com.umc.finly.domain.home.repository;

import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HomeMindRepository extends Repository<RecordEntry, Long> {

    //C. 기록 성실도
    @Query("""
        select distinct r.recordDate
        from RecordEntry r
        where r.memberId = :memberId
          and r.recordDate between :start and :end
    """)
    List<LocalDate> findDistinctRecordDates(
            Long memberId,
            LocalDate start,
            LocalDate end
    );

    // A. 하락장 공포지수 (최신 1개)
    @Query("""
        select f
        from FearIndexResult f
        where f.memberId = :memberId
        order by f.endDate desc
    """)
    List<FearIndexResult> findLatestFearIndexResults(Long memberId);

    // B. 매수 확신도 (최신 1개)
    @Query("""
        select c
        from ConvictionScoreResult c
        where c.memberId = :memberId
        order by c.endDate desc
    """)
    List<ConvictionScoreResult> findLatestConvictionScoreResults(Long memberId);

}
