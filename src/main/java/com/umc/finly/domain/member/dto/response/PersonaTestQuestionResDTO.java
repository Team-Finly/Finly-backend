package com.umc.finly.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PersonaTestQuestionResDTO {
    // 페르소나 질문 조회 응답 DTO

    private final Long id;
    private final String questionCode;
    private final String content;
    private final List<PersonaTestOptionResDTO> options;
}
