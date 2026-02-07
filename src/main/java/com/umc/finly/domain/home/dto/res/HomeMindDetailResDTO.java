package com.umc.finly.domain.home.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeMindDetailResDTO {

    private String memberName;

    private Persona persona;

    private int fmiScore;
    private String fmiLevel;
    private String fmiComment;

    private Scores scores;

    @Getter
    @Builder
    public static class Persona {
        private String personaTitle;
        private String description;
    }

    @Getter
    @Builder
    public static class Scores {
        private ScoreDetail downMarketResilience;
        private ScoreDetail decisionConsistency;
        private ScoreDetail recordConsistency;
    }

    @Getter
    @Builder
    public static class ScoreDetail {
        private int score;
        private String description;
    }
}

