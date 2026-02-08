package com.umc.finly.domain.analysis.association.entity;

import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * [매수 확신도] 코스피/코스닥 지수 히스토리
 * 코스닥/코스피 일별 지수 저장
 * → record_entry의 record_date 날짜와 기록한 종목의 market_type을 사용해 MarketIndexHistory에서 T와 T+5 지수 값을 찾아 비교
 * ※ T 지수: 기록은 특정 종목에 종속되어 있으므로, record_entry의 unit_price 대신 확신 매수 기록의 record_date 날짜에 해당하는 지수 값을 market_index_history에서 찾아 사용함.)
 */
@Entity
@Table(name = "market_index_history", indexes = {
        @Index(name = "idx_market_date", columnList = "market_type, base_date")
})
public class MarketIndexHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 시장 타입 (KOSPI/KOSDAQ)
    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false)
    private MarketType marketType;

    // 지수 값
    @Column(name = "index_value", nullable = false)
    private Double indexValue;

    // 데이터 기준일 (ex. 2026-02-12)
    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;
}
