package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.PersonaTestOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonaTestOptionResDTO {

    private Long id;
    private String choiceCode;
    private String content;

    public static PersonaTestOptionResDTO from(PersonaTestOption option){
        return PersonaTestOptionResDTO.builder()
                .id(option.getId())
                .choiceCode(option.getChoiceCode().name())
                .content(option.getContent())
                .build();
    }
}
