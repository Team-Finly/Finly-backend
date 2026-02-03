package com.umc.finly.domain.record.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmotionCode {
    CALM("평온", "으로"),
    ANXIETY("불안", "으로"),
    REGRET("후회", "로"),
    GREED("욕심", "으로"),
    CONFIDENCE("확신", "으로");

    private final String label;
    private final String particle;
}
