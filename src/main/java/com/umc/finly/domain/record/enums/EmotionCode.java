package com.umc.finly.domain.record.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 감정 코드 enum
// 기록 시 사용자가 선택하는 감정 타입
// label: 한글명, particle: 조사 (으로/로)
@Getter
@RequiredArgsConstructor
public enum EmotionCode {
    ANXIETY("불안", "으로"),    // 불안 - 걱정, 초조한 상태
    GREED("탐욕", "으로"),      // 탐욕 - 더 많은 수익을 원하는 상태
    CALM("평온", "으로"),       // 평온 - 차분하고 안정된 상태
    CONFIDENCE("확신", "으로"), // 확신 - 매매 결정에 대한 강한 믿음
    REGRET("후회", "로");       // 후회 - 매매 결정에 대한 아쉬움

    private final String label;    // 한글 표기명
    private final String particle; // 조사 (문장 생성용)
}
