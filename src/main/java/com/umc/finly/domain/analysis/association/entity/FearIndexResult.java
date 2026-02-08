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
 * [하락장 공포지수] 하락장 공포지수 결과
 * 최종 산출된 사용자별 하락장 공포지수 결과 저장
 * fear index = (f(t)/1.5) * 100
 * f(t) = (반응 세션 수 / 전체 세션 수) * 평균 시간 민감도 가중치
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "fear_index_result", indexes = {
        @Index(name = "idx_fear_result_member_date", columnList = "member_id, start_date, end_date")
})public class FearIndexResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 하락장 공포지수
    @Column(name = "fear_index", nullable = false)
    private Integer fearIndex;

    // 분석 기간
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 분석 기간 내 총 하락 세션 수
    @Column(name = "total_session_count", nullable = false)
    private Integer totalSessionCount;

    // 분석 기간 내 사용자가 반응한 세션 수 (반응 = 불안,후회 기록)
    @Column(name = "reacted_session_count", nullable = false)
    private Integer reactedSessionCount;

    // 평균 반응 시간
    @Column(name = "average_response_time", precision = 10, scale = 2)
    private BigDecimal averageResponseTime;

    // 평균 시간 민감도 가중치
    @Column(name = "average_time_weight")
    private Double averageTimeWeight;

//    // 하락장 공포지수 결과 속 포함된 세션별 상세 내역
//    @OneToMany(mappedBy = "fearIndexResult", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<FearIndexDetail> details = new ArrayList<>();
}
