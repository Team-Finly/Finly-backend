package com.umc.finly.domain.market.repository;

import com.umc.finly.domain.market.repository.projection.StockEmotionBuyAggregation;
import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MarketInsightRepository extends JpaRepository<RecordEntry, Long> {

    @Query(
            value = """
            SELECT
                r.stock_id      AS stockId,
                s.name          AS stockName,
                r.emotion_code  AS emotionCode,
                COUNT(r.id)     AS buyCount
            FROM record_entry r
            JOIN stock s ON r.stock_id = s.id
            WHERE r.trade_action = 'BUY'
              AND r.record_date >= :fromDate
              AND r.deleted_at IS NULL
              AND s.is_active = true
            GROUP BY r.stock_id, s.name, r.emotion_code
        """,
            nativeQuery = true
    )// 최근 N일 동안 유저들이 매수(BUY)할 때 느낀 감정을
// 종목 이름 기준으로 집계하여 실시간 인사이트 생성을 위한 쿼리

    List<StockEmotionBuyAggregation> aggregateBuyEmotionByStock(
            @Param("fromDate") LocalDate fromDate
    );
}
