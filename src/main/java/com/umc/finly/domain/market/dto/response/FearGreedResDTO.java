package com.umc.finly.domain.market.dto.response;

import com.umc.finly.domain.market.enums.FearGreedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FearGreedResDTO {
    private final Integer score;
    private final FearGreedStatus status;
}
