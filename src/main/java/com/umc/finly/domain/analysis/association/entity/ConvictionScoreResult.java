package com.umc.finly.domain.analysis.association.entity;

import com.umc.finly.domain.member.entity.Member;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * [매수 확신도] 매수 확신도 결과
 * 최종 산출된 매수 확신도 결과 저장
 * conviction score = (확신 적중 횟수 / 전체 확신 매수 횟수) × 100
 * 확신 적중 = T+5 주가 > 확신 매수 시점 가격
 */
@Entity
@Table(name = "conviction_score_result")
public class ConvictionScoreResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 매수 확신도
    @Column(name = "conviction_score", nullable = false)
    private Double convictionScore;

    // 분석 기간
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /* 확신 매수 통계 */
    // 분석 기간 내 총 확신 매수 기록 횟수
    private Integer totalConfidenceBuyCount = 0;
    // 분석 기간 내 확신 매수 적중 횟수
    private Integer totalConfidenceBuyHitCount = 0;
    // 확신 매수 적중률
    private Double confidenceBuyHitRate = 0.0;

    /* 탐욕 매수 통계 */
    // 분석 기간 내 총 탐욕 매수 기록 횟수
    private Integer totalGreedBuyCount = 0;
    // 분석 기간 내 탐욕 매수 적중 횟수
    private Integer totalGreedBuyHitCount = 0;
    // 탐욕 매수 적중률 (확신 매수 변별력 측정용)
    private Double greedBuyHitRate = 0.0;
}
