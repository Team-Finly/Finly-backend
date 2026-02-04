package com.umc.finly.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaTestSubmitReqDTO {
    //  페르소나 테스트 제출 요청 DTO

    private List<PersonaAnswerReqDTO> answers;
}
