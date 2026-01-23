package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.PersonasTestQuestion;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter@Builder
public class PersonasTestQuestionRes {

    private Long id;
    private String questionCode;
    private String content;
    private List<PersonasTestOptionRes> options;

    public static PersonasTestQuestionRes of(
            PersonasTestQuestion question,
            List<PersonasTestOptionRes> options
    ){
        return PersonasTestQuestionRes.builder()
                .id(question.getId())
                .questionCode(question.getQuestionCode().name())
                .content(question.getContent())
                .options(options)
                .build();
    }
}
