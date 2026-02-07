package com.umc.finly.domain.home.dto.res;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class HomeMindResDTO {

    private String memberName; // 사용자 닉네임

    private Persona persona; // 페르소나 타이틀

    private int fmiScore;      // FMI 점수
    private String fmiLevel;   // 평균적 관리
    private String fmiComment; // 한 줄 해석
    private Scores scores;     // A/B/C 숫자

    @Getter
    @Builder
    public static class Persona {
        private String personaType;
        private String personaTitle;
    }

    @Getter
    @Builder
    public static class Scores {
        private int resilience; //A
        private int decision; //B
        private int record; //C
    }
}
