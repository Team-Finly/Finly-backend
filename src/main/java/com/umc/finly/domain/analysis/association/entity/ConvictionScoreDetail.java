package com.umc.finly.domain.analysis.association.entity;

import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.enums.EmotionCode;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


/**
 * [매수 확신도] 매수 확신도 상세 정보
 * 분석 기간 내 포함된 여러 확신 매수 기록에 대해,
 * 각 기록의 정보
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "conviction_score_detail")
public class ConvictionScoreDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conviction_score_result_id", nullable = false)
    private ConvictionScoreResult convictionScoreResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_entry_id", nullable = false)
    private RecordEntry recordEntry;

    // 기록 감정 (확신 or 탐욕)
    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_code", nullable = false)
    private EmotionCode emotionCode;

    // 기록일(T)의 시장 지수 값
    @Column(name = "base_index_value", nullable = false)
    private Double baseIndexValue;

    // 5영업일 뒤(T+5)의 시장 지수 값
    @Column(name = "target_index_value", nullable = false)
    private Double targetIndexValue;

    // 적중 여부 (targetIndexValue > baseIndexValue)
    @Column(name = "is_hit", nullable = false)
    private Boolean isHit;
}
