package com.umc.finly.domain.analysis.stock.enums;

/**
 * 가격대 분포 구간 타입
 *
 * - LOW : 평균 매수가 대비 낮은 가격대
 * - MID : 평균 매수가 ± 정책 퍼센트 범위(5%) (사용자가 가장 자주 판단한 가격대)
 * - HIGH: 평균 매수가 대비 높은 가격대
 */
public enum PriceRangeType {
    LOW,
    MID,
    HIGH
}
