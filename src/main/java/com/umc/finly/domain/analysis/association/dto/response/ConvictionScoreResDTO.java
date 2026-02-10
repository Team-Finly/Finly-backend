package com.umc.finly.domain.analysis.association.dto.response;

import com.umc.finly.domain.analysis.association.enums.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConvictionScoreResDTO {

    // 매수 확신도
    private int convictionScore;

    // 점수 구간별 상태 라벨
    private Status status;

    // 점수 구간별 노출 문구
    private String phrase;
}
