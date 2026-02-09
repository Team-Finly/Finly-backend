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

// 조각 모음함(Fragment) 조회용 Repository
// RecordEntry를 감정별로 그룹핑하여 통계 및 리스트 조회를 담당함
// 참고: RecordEntry 테이블을 사용하지만 조각 모음함 전용 쿼리만 정의함
public interface FragmentRepository extends JpaRepository<RecordEntry, Long> {

    // 회원의 전체 기록 개수 조회
    long countByMemberId(Long memberId);

    // 감정 타입별 기록 개수 집계 (조각 모음함 요약용)
    @Query("""
        select r.emotionCode as emotionCode, count(r) as count
        from RecordEntry r
        where r.memberId = :memberId
        group by r.emotionCode
    """)
    List<EmotionCountProjection> countGroupByEmotionCode(@Param("memberId") Long memberId);

    // 조각 리스트 조회 (감정/기간 필터 + 최신순 정렬)
    // Stock 조인하여 종목 정보도 함께 조회
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

    // 조각 리스트 개수 조회 (findFragmentList와 동일 조건)
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

    // 캘린더용 날짜별/감정별 조각 개수 집계
    // 특정 기간(from~to) 내 recordDate + emotionCode 기준으로 그룹핑하여 count를 반환
    @Query("""
    select r.recordDate as recordDate, r.emotionCode as emotionCode, count(r) as count
    from RecordEntry r
    where r.memberId = :memberId
      and r.recordDate >= :fromDate
      and r.recordDate <= :toDate
    group by r.recordDate, r.emotionCode
    order by r.recordDate asc
    """)
    List<CalendarCountProjection> countCalendarByDateAndEmotion(
            @Param("memberId") Long memberId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 캘린더 범위(from~to) 내 전체 기록 수 조회
    @Query("""
    select count(r)
    from RecordEntry r
    where r.memberId = :memberId
      and r.recordDate >= :fromDate
      and r.recordDate <= :toDate
      and r.deletedAt is null
    """)
    long countByMemberIdAndRecordDateBetween(
            @Param("memberId") Long memberId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    // ===== Projection Interfaces =====

    // 감정별 개수 집계 결과를 받기 위한 Projection
    interface EmotionCountProjection {
        EmotionCode getEmotionCode(); // 감정 코드
        Long getCount();              // 해당 감정의 기록 개수
    }

    // 조각 리스트 조회 결과를 담는 Projection (Stock 조인 포함)
    interface FragmentListRowView {
        LocalDate getRecordDate();    // 기록 날짜
        Long getFragmentId();         // 조각(기록) ID
        Long getStockId();            // 종목 ID
        String getStockName();        // 종목명
        TradeAction getTradeAction(); // 매매 타입 (BUY/SELL/WATCH)
        BigDecimal getUnitPrice();    // 단가
        BigDecimal getQuantity();     // 수량
        String getMemo();             // 메모
        EmotionCode getEmotionCode(); // 감정 코드
    }

    // 캘린더 집계 결과를 받기 위한 Projection
    interface CalendarCountProjection {
        LocalDate getRecordDate();    // 기록 날짜
        EmotionCode getEmotionCode(); // 감정 코드
        Long getCount();              // 해당 날짜/감정의 기록 개수
    }
}
