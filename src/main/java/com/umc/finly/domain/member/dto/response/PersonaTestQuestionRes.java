package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PersonaTestQuestionRes {

    private Long id;
    private String questionCode;
    private String content;
    private List<PersonaTestOptionRes> options;

    public static PersonaTestQuestionRes of(
            PersonaTestQuestion question,
            List<PersonaTestOptionRes> options
    ){
        return PersonaTestQuestionRes.builder()
                .id(question.getId())
                .questionCode(question.getQuestionCode().name())
                .content(question.getContent())
                .options(options)
                .build();
    }
}
