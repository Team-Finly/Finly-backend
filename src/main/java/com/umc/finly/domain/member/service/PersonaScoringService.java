package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.request.PersonaAnswerReq;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.PersonaTestOption;
import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import com.umc.finly.domain.member.enums.ChoiceCode;
import com.umc.finly.domain.member.enums.PersonaType;
import com.umc.finly.domain.member.enums.QuestionCode;
import com.umc.finly.domain.member.exception.MemberErrorCode;
import com.umc.finly.domain.member.repository.PersonaRepository;
import com.umc.finly.domain.member.repository.PersonasTestOptionRepository;
import com.umc.finly.domain.member.repository.PersonasTestQuestionRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.umc.finly.domain.member.enums.ChoiceCode.*;
import static com.umc.finly.domain.member.enums.QuestionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonaScoringService {

    private final PersonasTestQuestionRepository questionRepository;
    private final PersonasTestOptionRepository optionRepository;
    private final PersonaRepository personaRepository;

    /**
     * Q1~Q3 답안을 검증하고 최종 Persona 엔티티를 반환한다.
     * - 질문/선택지 존재 여부
     * - 선택지가 해당 질문에 속하는지
     * - Q1~Q3 전부 제출 여부
     */
    public Persona resolvePersona(List<PersonaAnswerReq> answers) {
        validateAnswersRequired(answers);

        Map<QuestionCode, ChoiceCode> answerMap = validateAndMapAnswers(answers);

        PersonaType personaType = decidePersona(answerMap);

        return personaRepository.findByPersonaType(personaType)
                .orElseThrow(() -> new CustomException(MemberErrorCode.PERSONA_RESULT_NOT_FOUND));
    }

    private void validateAnswersRequired(List<PersonaAnswerReq> answers) {
        if (answers == null || answers.size() != 3) {
            throw new CustomException(MemberErrorCode.PERSONA_ANSWERS_REQUIRED);
        }
    }

    /**
     * questionId -> QuestionCode, optionId -> ChoiceCode 를 매핑하며 정합성 검증을 수행한다.
     */
    private Map<QuestionCode, ChoiceCode> validateAndMapAnswers(List<PersonaAnswerReq> answers) {
        Map<QuestionCode, ChoiceCode> answerMap = new EnumMap<>(QuestionCode.class);

        for (PersonaAnswerReq answer : answers) {
            PersonaTestQuestion question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new CustomException(MemberErrorCode.INVALID_PERSONA_ANSWER));

            PersonaTestOption option = optionRepository.findById(answer.getOptionId())
                    .orElseThrow(() -> new CustomException(MemberErrorCode.INVALID_PERSONA_ANSWER));

            // option이 해당 question에 속하는지 검증
            if (!option.getQuestion().getId().equals(question.getId())) {
                throw new CustomException(MemberErrorCode.INVALID_PERSONA_ANSWER);
            }

            answerMap.put(question.getQuestionCode(), option.getChoiceCode());
        }

        // Q1~Q3 모두 제출했는지 검증(중복 제출/누락 방지)
        if (!answerMap.containsKey(QuestionCode.Q1)
                || !answerMap.containsKey(QuestionCode.Q2)
                || !answerMap.containsKey(QuestionCode.Q3)) {
            throw new CustomException(MemberErrorCode.PERSONA_ANSWERS_REQUIRED);
        }

        return answerMap;
    }

    // 페르소나 결정 로직
    private PersonaType decidePersona(Map<QuestionCode, ChoiceCode> a) {
        if (a.get(Q1) == B) return PersonaType.WORRIED_DEER;
        if (a.get(Q3) == A) return PersonaType.CAUTIOUS_TURTLE;
        if (a.get(Q3) == C) return PersonaType.SHARP_EAGLE;
        return PersonaType.FIERY_LION;
    }
}

