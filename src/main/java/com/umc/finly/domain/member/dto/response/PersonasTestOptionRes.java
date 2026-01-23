package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.PersonasTestOption;
import lombok.Builder;
import lombok.Getter;

@Getter@Builder
public class PersonasTestOptionRes {

    private Long id;
    private String choiceCode;
    private String content;

    public static PersonasTestOptionRes from(PersonasTestOption option){
        return PersonasTestOptionRes.builder()
                .id(option.getId())
                .choiceCode(option.getChoiceCode().name())
                .content(option.getContent())
                .build();
    }
}
