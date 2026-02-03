package com.umc.finly.domain.market.repository;

import com.umc.finly.domain.market.repository.projection.StockEmotionBuyAggregation;
import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MarketInsightRepository extends JpaRepository<RecordEntry, Long> {

    @Query("""
        SELECT r.stockId           AS stockId,
               s.name              AS stockName,
               r.emotionCode       AS emotionCode,
               COUNT(r.id)         AS buyCount
        FROM RecordEntry r
        JOIN Stock s ON r.stockId = s.id
        WHERE r.tradeAction = com.umc.finly.domain.record.enums.TradeAction.BUY
          AND r.recordDate >= :fromDate
          AND r.deletedAt IS NULL
          AND s.isActive = true
        GROUP BY r.stockId, s.name, r.emotionCode
    """)// 최근 N일 동안 유저들이 매수(BUY)할 때 느낀 감정을
// 종목 이름 기준으로 집계하여 실시간 인사이트 생성을 위한 쿼리

    List<StockEmotionBuyAggregation> aggregateBuyEmotionByStock(
            @Param("fromDate") LocalDate fromDate
    );
}
