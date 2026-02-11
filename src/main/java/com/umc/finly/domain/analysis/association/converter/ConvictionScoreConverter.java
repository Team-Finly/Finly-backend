package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.dto.response.ConvictionScoreResDTO;
import com.umc.finly.domain.analysis.association.enums.Status;

public class ConvictionScoreConverter {

    public static ConvictionScoreResDTO toConvictionScoreResDTO(int score) {
        Status status;
        String phrase;

        if (score >= 85) {
            status = Status.HIGH;
            phrase = "확신 판단 정확도 높음";
        } else if (score >= 70) {
            status = Status.GOOD;
            phrase = "확신 판단 신뢰도 양호";
        } else if (score >= 50) {
            status = Status.MID;
            phrase = "판단 신뢰도 불안정";
        } else {
            status = Status.LOW;
            phrase = "충동적 결정 주의";
        }

        return ConvictionScoreResDTO.builder()
                .convictionScore(score)
                .status(status)
                .phrase(phrase)
                .build();
    }
}