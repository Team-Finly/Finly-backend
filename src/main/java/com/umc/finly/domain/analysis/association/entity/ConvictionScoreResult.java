package com.umc.finly.domain.analysis.association.entity;

import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * [매수 확신도] 매수 확신도 결과
 * 최종 산출된 매수 확신도 결과 저장
 * conviction score = (확신 적중 횟수 / 전체 확신 매수 횟수) × 100
 * 확신 적중 = T+5 주가 > 확신 매수 시점 가격
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "conviction_score_result", indexes = {
        @Index(name = "idx_conviction_result_member_date", columnList = "member_id, start_date, end_date")
})public class ConvictionScoreResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 매수 확신도
    @Column(name = "conviction_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal convictionScore;

    // 분석 기간
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /* 확신 매수 통계 */
    // 분석 기간 내 총 확신 매수 기록 횟수
    @Column(name = "total_confidence_buy_count", nullable = false)
    private Integer totalConfidenceBuyCount = 0;
    // 분석 기간 내 확신 매수 적중 횟수
    @Column(name = "total_confidence_buy_hit_count", nullable = false)
    private Integer totalConfidenceBuyHitCount = 0;
    // 확신 매수 적중률
    @Column(name = "confidence_buy_hit_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidenceBuyHitRate = BigDecimal.ZERO;

    /* 탐욕 매수 통계 */
    // 분석 기간 내 총 탐욕 매수 기록 횟수
    @Column(name = "total_greed_buy_count")
    private Integer totalGreedBuyCount = 0;
    // 분석 기간 내 탐욕 매수 적중 횟수
    @Column(name = "total_greed_buy_hit_count")
    private Integer totalGreedBuyHitCount = 0;
    // 탐욕 매수 적중률 (확신 매수 변별력 측정용)
    @Column(name = "greed_buy_hit_rate", precision = 5, scale = 2)
    private BigDecimal greedBuyHitRate = BigDecimal.ZERO;

    // 매수 확신도 결과 속 포함된 기록별 상세 내역
    @OneToMany(mappedBy = "convictionScoreResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConvictionScoreDetail> details = new ArrayList<>();
}
