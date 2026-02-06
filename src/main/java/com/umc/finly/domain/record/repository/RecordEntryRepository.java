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

public interface RecordEntryRepository extends JpaRepository<RecordEntry, Long> {
    boolean existsByClientRequestId(String clientRequestId);
    List<RecordEntry> findByMemberIdOrderByRecordDateDesc(Long memberId, Pageable pageable);
    List<RecordEntry> findByMemberIdAndRecordDateOrderByCreatedAtAsc(Long memberId, LocalDate recordDate);

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

    List<RecordEntry> findAllByMemberIdAndStockId(Long memberId, Long stockId);
    List<RecordEntry> findAllByMemberIdAndStockIdAndTradeAction(Long memberId, Long stockId, TradeAction tradeAction);

    // 사용자가 가장 많이 기록한 종목 찾기
    @Query("""
        select r.stockId
        from RecordEntry r
        where r.memberId = :memberId
        group by r.stockId
        order by count(r.id) desc, max(r.createdAt) desc
        """)
    List<Long> findTopStockByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    /**
     * recordDate을 기준으로 특정 종목에 대해 특정 기간 내의 모든 기록 조회
     * @param stockId 종목 PK
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 검색된 기록 리스트
     */
    List<RecordEntry> findAllByMemberIdAndStockIdAndRecordDateBetween(Long memberId, Long stockId, LocalDate startDate, LocalDate endDate);
}
