package com.umc.finly.domain.analysis.association.entity;

import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [하락장 공포지수] 코스피/코스닥 전일 대비율 히스토리
 * 1분마다 수집되는 코스피/코스닥 전일 대비율을 저장 -> 하락 세션 식별
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "market_change_rate_history", indexes = {
        @Index(name = "idx_market_datetime", columnList = "market_type, base_date_time")
})
public class MarketChangeRateHistory extends CreatedUpdatedDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 시장 타입 (KOSPI/KOSDAQ)
    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false)
    private MarketType marketType;

    // 전일 대비율 (ex. -1.25%)
    @Column(name = "change_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal changeRate;

    // 데이터 기준일 (ex. 2026-02-07 14:05)
    @Column(name = "base_date_time", nullable = false)
    private LocalDateTime baseDateTime;
}
