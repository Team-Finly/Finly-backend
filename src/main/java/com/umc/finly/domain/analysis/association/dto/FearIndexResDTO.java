package com.umc.finly.domain.analysis.association.dto;

import com.umc.finly.domain.analysis.association.enums.ChangeDirection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FearIndexResDTO {

    // 하락장 공포지수
    private int fearIndex;

    // 사용자의 직전 하락장 공포지수 대비 방향
    private ChangeDirection changeDirection;

    // 직전 대비 변화량(절댓값)
    private int changeValue;

    // 하락장 공포지수 구간별 노출 문구
    private String phrase;
}
