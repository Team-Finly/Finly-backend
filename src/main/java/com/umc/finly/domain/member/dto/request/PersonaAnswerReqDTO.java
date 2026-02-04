package com.umc.finly.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaAnswerReqDTO {
    // 페르소나 질문별 선택 답안 DTO

    private Long questionId;
    private Long optionId;
}
