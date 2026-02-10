package com.umc.finly.domain.member.converter;

import com.umc.finly.domain.member.dto.response.PersonaTestOptionResDTO;
import com.umc.finly.domain.member.dto.response.PersonaTestQuestionResDTO;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitResDTO;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.PersonaTestOption;
import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonaTestConverter {

    public static PersonaTestOptionResDTO toOptionResDTO(PersonaTestOption option) {
        return new PersonaTestOptionResDTO(
                option.getId(),
                option.getChoiceCode().name(),
                option.getContent()
        );
    }

    public static PersonaTestQuestionResDTO toQuestionResDTO(
            PersonaTestQuestion question,
            List<PersonaTestOptionResDTO> options
    ) {
        return new PersonaTestQuestionResDTO(
                question.getId(),
                question.getQuestionCode().name(),
                question.getContent(),
                options
        );
    }

    public static PersonaTestSubmitResDTO toSubmitResDTO(
            Persona persona,
            boolean saved,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new PersonaTestSubmitResDTO(
                persona.getPersonaType().toUiType(),
                saved,
                createdAt,
                updatedAt
        );
    }
}