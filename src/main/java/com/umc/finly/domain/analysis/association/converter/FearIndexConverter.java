package com.umc.finly.domain.analysis.association.converter;

import com.umc.finly.domain.analysis.association.enums.ChangeDirection;
import com.umc.finly.domain.analysis.association.dto.response.FearIndexResDTO;

public class FearIndexConverter {

    public static FearIndexResDTO toFearIndexResDTO(int currentScore, int previousScore) {

        ChangeDirection direction = determineDirection(currentScore, previousScore);
        int changeValue = Math.abs(currentScore - previousScore);
        String phrase = determinePhrase(currentScore);

        return FearIndexResDTO.builder()
                .fearIndex(currentScore)
                .changeDirection(direction)
                .changeValue(changeValue)
                .phrase(phrase)
                .build();
    }

    private static ChangeDirection determineDirection(int current, int previous) {
        if (current > previous) return ChangeDirection.UP;
        if (current < previous) return ChangeDirection.DOWN;
        return ChangeDirection.SAME;
    }

    private static String determinePhrase(int index) {
        if (80 <= index && index <= 100) return "패닉 반응 가능성 높음";
        if (60 <= index && index <= 79) return "평소보다 불안에 예민";
        if (40 <= index && index <= 59) return "하락장에 감정 반응 증가";
        if (20 <= index && index <= 39) return "비교적 차분한 반응";
        if (0 <= index && index <= 19) return "하락장에서도 감정 변화 적음";
        return "범위 외";
    }
}