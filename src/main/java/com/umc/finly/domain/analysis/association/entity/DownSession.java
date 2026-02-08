package com.umc.finly.domain.analysis.association.entity;

import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [하락장 공포지수] 하락 세션
 * 하락 세션 판별해서 저장
 * (전일 대비율이 -1.0% 이하로 떨어진 시점) ~ (-1.0% 이상으로 오른 시점)까지의 구간을 하나의 하락 세션으로 정의
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "down_session", indexes = {
        @Index(name = "idx_down_session_market_date", columnList = "market_type, session_date")
})
public class DownSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 시장 타입 (KOSPI/KOSDAQ)
    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false)
    private MarketType marketType;

    // 세션 발생 일자 (ex. 2026-02-07)
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    // 하락 시작 시점 (ex. 2026-02-07 14:05) (-1.0% 이하 진입한 시점)
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    // 하락 종료 시점 (ex. 2026-02-07 14:32) (-1.0% 이상 혹은 장 마감 시점)
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;
}