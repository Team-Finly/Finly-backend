package com.umc.finly.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonaTestOptionResDTO {

    private final Long id;
    private final String choiceCode;
    private final String content;
}
