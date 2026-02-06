package com.umc.finly.domain.analysis.stock.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umc.finly.domain.analysis.stock.enums.PriceRangeType;

import java.util.List;

public record PriceDistributionResDTO(
        int averageBuyPrice,     // 평균 매수가
        RangePolicy rangePolicy, // 가격대 분포 산출 정책
        List<PriceDistributionItem> distributions
) {
    public record RangePolicy(
            String type, // ex. "AVERAGE_BUY_PRICE"
            int percent // ex. 5
    ) {}

    public record PriceDistributionItem(
            PriceRangeType rangeType, // LOW / MID / HIGH
            String displayRange,      // "70,000원 ~ 75,000원"
            int count,
            int ratio,

            @JsonInclude(JsonInclude.Include.NON_NULL) // 값이 null인 필드는 응답에서 제외
            Boolean isFocused // 강조 표시 대상만 true
    ) {}
}