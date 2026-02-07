package com.umc.finly.domain.home.repository;

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


    // 기간 내 전체 기록
    List<RecordEntry> findByMemberIdAndRecordDateBetween(
            Long memberId,
            LocalDate start,
            LocalDate end
    );

    // 확신 + 매수 기록
    List<RecordEntry> findByMemberIdAndEmotionCodeAndTradeAction(
            Long memberId,
            EmotionCode emotionCode,
            TradeAction tradeAction
    );

    // 월 기록 일자레
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
}
