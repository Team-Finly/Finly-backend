package com.umc.finly.domain.market.dto;

import com.umc.finly.domain.market.enums.FearGreedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FearGreedResult {
    private final Integer score;
    private final FearGreedStatus status;
}
