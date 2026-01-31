package com.umc.finly.domain.market.enums;

public enum FearGreedStatus {
    EXTREME_FEAR,
    FEAR,
    NEUTRAL,
    GREED,
    EXTREME_GREED;

    public static FearGreedStatus getFearGreedStatus(String rating) {
        return FearGreedStatus.valueOf(
                rating.toUpperCase().replace(" ", "_")
        );
    }
}
