package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FragmentRepository extends JpaRepository<RecordEntry, Long> {
    long countByMemberId(Long memberId); // 해당 회원이 기록한 전체 기록 개수

    // 회원의 기록을 감정 타입(emotionCode) 기준으로 그룹핑하여 개수 집계
    @Query("""
        select r.emotionCode as emotionCode, count(r) as count
        from RecordEntry r
        where r.memberId = :memberId
        group by r.emotionCode
    """)
    List<EmotionCountProjection> countGroupByEmotionCode(@Param("memberId") Long memberId);

    // 감정/기간 필터 적용 + 최신순 정렬로 조각 리스트 조회
    @Query("""
        select 
            r.recordDate as recordDate,
            r.id as fragmentId,
            s.id as stockId,
            s.name as stockName,
            r.tradeAction as tradeAction,
            r.unitPrice as unitPrice,
            r.quantity as quantity,
            r.memo as memo,
            r.emotionCode as emotionCode
        from RecordEntry r
        join Stock s on s.id = r.stockId
        where r.memberId = :memberId
          and (:boxType is null or r.emotionCode = :boxType)
          and (:fromDate is null or r.recordDate >= :fromDate)
          and (:toDate is null or r.recordDate <= :toDate)
        order by r.recordDate desc, r.id desc
    """)
    List<FragmentListRowView> findFragmentList(
            @Param("memberId") Long memberId,
            @Param("boxType") EmotionCode boxType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 리스트와 동일 조건으로 총 개수 카운트
    @Query("""
        select count(r)
        from RecordEntry r
        where r.memberId = :memberId
          and (:boxType is null or r.emotionCode = :boxType)
          and (:fromDate is null or r.recordDate >= :fromDate)
          and (:toDate is null or r.recordDate <= :toDate)
    """)
    long countFragmentList(
            @Param("memberId") Long memberId,
            @Param("boxType") EmotionCode boxType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // ===== Projection interfaces =====

    // 감정 타입별 개수 조회 결과를 받기 위한 Projection
    interface EmotionCountProjection {
        EmotionCode getEmotionCode();
        Long getCount();
    }

    // 조각 리스트 조회 결과를 담는 Projection(Stock 조인 포함)
    interface FragmentListRowView {
        LocalDate getRecordDate();
        Long getFragmentId();
        Long getStockId();
        String getStockName();
        TradeAction getTradeAction();
        BigDecimal getUnitPrice();
        BigDecimal getQuantity();
        String getMemo();
        EmotionCode getEmotionCode();
    }
}
