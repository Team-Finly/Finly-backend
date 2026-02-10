package com.umc.finly.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonaTestOptionResDTO {
    // 페르소나 선택지 응답 DTO

    private final Long id;
    private final String choiceCode;
    private final String content;
}
