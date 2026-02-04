package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PersonaTestQuestionResDTO {

    private Long id;
    private String questionCode;
    private String content;
    private List<PersonaTestOptionResDTO> options;

    public static PersonaTestQuestionResDTO of(
            PersonaTestQuestion question,
            List<PersonaTestOptionResDTO> options
    ){
        return PersonaTestQuestionResDTO.builder()
                .id(question.getId())
                .questionCode(question.getQuestionCode().name())
                .content(question.getContent())
                .options(options)
                .build();
    }
}
