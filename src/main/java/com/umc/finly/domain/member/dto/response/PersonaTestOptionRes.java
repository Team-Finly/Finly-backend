package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.PersonaTestOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonaTestOptionRes {

    private Long id;
    private String choiceCode;
    private String content;

    public static PersonaTestOptionRes from(PersonaTestOption option){
        return PersonaTestOptionRes.builder()
                .id(option.getId())
                .choiceCode(option.getChoiceCode().name())
                .content(option.getContent())
                .build();
    }
}
