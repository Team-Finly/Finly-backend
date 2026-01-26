package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.request.PersonaAnswerReq;
import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReq;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitRes;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.PersonaTestOption;
import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import com.umc.finly.domain.member.entity.mapping.MemberPersonaResult;
import com.umc.finly.domain.member.enums.ChoiceCode;
import com.umc.finly.domain.member.enums.PersonaType;
import com.umc.finly.domain.member.enums.QuestionCode;
import com.umc.finly.domain.member.exception.MemberErrorCode;
import com.umc.finly.domain.member.repository.MemberPersonaResultRepository;
import com.umc.finly.domain.member.repository.PersonaRepository;
import com.umc.finly.domain.member.repository.PersonasTestOptionRepository;
import com.umc.finly.domain.member.repository.PersonasTestQuestionRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

import static com.umc.finly.domain.member.enums.ChoiceCode.*;
import static com.umc.finly.domain.member.enums.QuestionCode.Q1;
import static com.umc.finly.domain.member.enums.QuestionCode.Q3;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaTestSubmitServiceImpl implements PersonaTestSubmitService{
    private final PersonasTestQuestionRepository questionRepository;
    private final PersonasTestOptionRepository optionRepository;
    private final MemberPersonaResultRepository memberPersonaResultRepository;
    private final PersonaRepository personaRepository;

    @Override
    public PersonaTestSubmitRes submit(String mode, PersonaTestSubmitReq request){

        // answers 개수 검증
        if(request.getAnswers().size() != 3){
            throw new CustomException(MemberErrorCode.PERSONA_ANSWERS_REQUIRED);
        }

        // questionsId -> ChoiceCode 매핑
        Map<QuestionCode, ChoiceCode> answerMap = new EnumMap<>(QuestionCode.class);

        for (PersonaAnswerReq answer : request.getAnswers()) {
            PersonaTestQuestion question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(()-> new CustomException(MemberErrorCode.INVALID_PERSONA_ANSWER));

            PersonaTestOption option = optionRepository.findById(answer.getOptionId())
                    .orElseThrow(()-> new CustomException(MemberErrorCode.INVALID_PERSONA_ANSWER));

            if(!option.getQuestion().getId().equals(question.getId())) {
                throw new CustomException(MemberErrorCode.INVALID_PERSONA_ANSWER);
            }

            answerMap.put(question.getQuestionCode(), option.getChoiceCode());
        }

        // 페르소나 결정 로직
        PersonaType resultType = decidePersona(answerMap);

        Persona persona = personaRepository.findByPersonaType(resultType)
                .orElseThrow(()-> new CustomException(MemberErrorCode.PERSONA_RESULT_NOT_FOUND));

        // mode 분기
        if ("signup".equals(mode)) {
            return buildResponse(persona, false, null, null);
        }

        if ("retest".equals(mode)) {
            Long memberId = SecurityUtil.getCurrentMemberId(); // ← JWT

            MemberPersonaResult result =
                    memberPersonaResultRepository.findByMemberId(memberId)
                            .map(existing -> existing.updatePersona(persona))
                            .orElseGet(() -> MemberPersonaResult.create(memberId, persona));

            memberPersonaResultRepository.save(result);

            return buildResponse(
                    persona,
                    true,
                    result.getCreatedAt(),
                    result.getUpdatedAt()
            );
        }

        throw new CustomException(MemberErrorCode.INVALID_PERSONA_ANSWER);
    }

    private PersonaType decidePersona(Map<QuestionCode, ChoiceCode> a) {

        if (a.get(Q1) == B) return PersonaType.WORRIED_DEER;
        if (a.get(Q3) == A) return PersonaType.CAUTIOUS_TURTLE;
        if (a.get(Q3) == C) return PersonaType.SHARP_EAGLE;
        return PersonaType.FIERY_LION;
    }

    private PersonaTestSubmitRes buildResponse(
            Persona persona,
            boolean saved,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return PersonaTestSubmitRes.builder()
                .persona(
                        PersonaTestSubmitRes.PersonaRes.builder()
                                .id(persona.getId())
                                .title(persona.getTitle())
                                .description(persona.getDescription())
                                .iconUrl(persona.getIconUrl())
                                .build()
                )
                .saved(saved)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
