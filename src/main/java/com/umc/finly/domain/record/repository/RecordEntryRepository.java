package com.umc.finly.domain.record.repository;

import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.domain.record.enums.TradeAction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// 투자 기록(RecordEntry) 엔티티의 데이터 접근 레이어
// 기록 CRUD, 검색, 통계 관련 쿼리를 담당함
public interface RecordEntryRepository extends JpaRepository<RecordEntry, Long> {

    // clientRequestId 중복 체크 (멱등성 보장용)
    boolean existsByClientRequestId(String clientRequestId);

    // 회원의 기록 조회 (최신순, 페이징)
    List<RecordEntry> findByMemberIdOrderByRecordDateDesc(Long memberId, Pageable pageable);

    // 특정 날짜의 기록 조회 (생성 시간 오름차순) - 오늘의 기록 API용
    List<RecordEntry> findByMemberIdAndRecordDateOrderByCreatedAtAsc(Long memberId, LocalDate recordDate);

    // 기록 검색 (감정 필터 + 키워드/종목 검색)
    // 메모 LIKE 검색 OR 종목 ID IN 검색 (종목명 매칭은 서비스 레이어에서 처리)
    @Query("SELECT r FROM RecordEntry r WHERE r.memberId = :memberId" +
            " AND (:emotionCode IS NULL OR r.emotionCode = :emotionCode)" +
            " AND (:keyword IS NULL OR r.memo LIKE CONCAT('%', :keyword, '%')" +
            "      OR (:stockIds IS NOT NULL AND r.stockId IN :stockIds))" +
            " ORDER BY r.createdAt DESC")
    List<RecordEntry> searchRecords(
            @Param("memberId") Long memberId,
            @Param("emotionCode") EmotionCode emotionCode,
            @Param("keyword") String keyword,
            @Param("stockIds") List<Long> stockIds);

    // 특정 종목의 모든 기록 조회
    List<RecordEntry> findAllByMemberIdAndStockId(Long memberId, Long stockId);

    // 특정 종목 + 매매 타입의 기록 조회
    List<RecordEntry> findAllByMemberIdAndStockIdAndTradeAction(Long memberId, Long stockId, TradeAction tradeAction);

    // 사용자가 가장 많이 기록한 종목 ID 조회 (기록 수 내림차순, 동률 시 최신순)
    @Query("""
        select r.stockId
        from RecordEntry r
        where r.memberId = :memberId
        group by r.stockId
        order by count(r.id) desc, max(r.createdAt) desc
        """)
    List<Long> findTopStockByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
