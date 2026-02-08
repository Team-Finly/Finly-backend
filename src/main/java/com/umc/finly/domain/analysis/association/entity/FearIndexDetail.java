package com.umc.finly.domain.analysis.association.entity;

import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * [하락장 공포지수] 하락장 공포지수 상세 정보
 * 분석 기간 내 포함된 여러 하락 세션에 대해,
 * 각 하락 세션의 사용자 기록과 반응 정보
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "fear_index_detail")
public class FearIndexDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fear_index_result_id")
    private FearIndexResult fearIndexResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_session_id")
    private DownSession downSession;

    // 해당 세션 내 불안 기록 횟수
    @Column(name = "anxiety_count")
    private Integer anxietyCount;

    // 해당 세션 내 후회 기록 횟수
    @Column(name = "regret_count")
    private Integer regretCount;

    // 해당 세션 내 기록들 중 가장 빨랐던 대표 반응 시간 1개
    @Column(name = "fastest_response_time")
    private Integer fastestResponseTime;

    // 해당 세션 내 기록들 중 가장 빨랐던 반응 시간에 대한 시간 민감도 가중치
    @Column(name = "applied_time_weight")
    private Double appliedTimeWeight;
}
