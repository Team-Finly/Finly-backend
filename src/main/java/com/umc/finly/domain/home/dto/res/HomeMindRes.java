package com.umc.finly.domain.home.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeMindRes {

    private String userName;       // 사용자 닉네임
    private String personaTitle;   // 페르소나 타이틀
    private int mindIndex;         // FMI 점수
    private String grade;          // 평균적 대응
    private String description;    // 해석 문구
}
