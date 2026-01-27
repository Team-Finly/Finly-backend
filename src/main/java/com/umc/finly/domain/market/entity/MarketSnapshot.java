package com.umc.finly.domain.market.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_snapshot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MarketSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 스냅샷 기준 시각
     * (예: 2026-01-25 13:00)
     */
    @Column(name = "snapshot_at", nullable = false)
    private LocalDateTime snapshotAt;

    /**
     * 코스피 지수
     */
    @Column(name = "kospi", precision = 10, scale = 2)
    private BigDecimal kospi;

    /**
     * 코스닥 지수
     */
    @Column(name = "kosdaq", precision = 10, scale = 2)
    private BigDecimal kosdaq;

    /**
     * 공포·탐욕 지수
     */
    @Column(name = "fear_greed_index")
    private Integer fearGreedIndex;

    /**
     * 공포 / 중립 / 탐욕 (enum으로 처리할 까 했는데 일단은 문자열로 두겠습니다)
     */
    @Column(name = "fear_greed_status", length = 20)
    private String fearGreedStatus;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 엔티티 저장 전 자동 세팅
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
