package com.umc.finly.domain.home.dto.res;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class HomeMindResDTO {

    private String nickname; // 사용자 닉네임

    private Persona persona; // 페르소나 타이틀

    private int fmi; // FMI 점수
    private String levelMessage; //해석 문구
    private Scores scores; //점수

    @Getter
    @Builder
    public static class Persona {
        private String personaType;
        private String title;
    }

    @Getter
    @Builder
    public static class Scores {
        private int resilience; //A
        private int decision; //B
        private int record; //C
    }
}
