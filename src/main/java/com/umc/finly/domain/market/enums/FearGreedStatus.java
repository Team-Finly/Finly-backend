package com.umc.finly.domain.market.enums;

public enum FearGreedStatus {
    EXTREME_FEAR,
    FEAR,
    NEUTRAL,
    GREED,
    EXTREME_GREED;

    public static FearGreedStatus getFearGreedStatus(String rating) {
        if (rating == null || rating.trim().isEmpty()) {
            throw new IllegalArgumentException("rating can't be null or empty");
        }

        String normalizedRating = rating.toUpperCase().replace(" ", "_");

        for (FearGreedStatus status : FearGreedStatus.values()) {
            if (status.name().equals(normalizedRating)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unexpected fear/greed rating: " + rating);
    }
}
