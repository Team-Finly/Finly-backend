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
    private final PersonaScoringService personaScoringService;
    private final MemberPersonaResultRepository memberPersonaResultRepository;

    @Override
    public PersonaTestSubmitRes submit(String mode, PersonaTestSubmitReq request){

        // 공용 채점/검증 로직 재사용
        Persona persona = personaScoringService.resolvePersona(request.getAnswers());

        // mode 분기
        if ("signup".equals(mode)) {
            // 회원가입 전 페르소나 테스트 결과 미리보기: 저장 안 됨
            return buildResponse(persona, false, null, null);
        }

        if ("retest".equals(mode)) {
            // 로그인 후 재테스트: JWT필요
            Long memberId = SecurityUtil.getCurrentMemberId();

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

        throw new CustomException(MemberErrorCode.INVALID_PERSONA_MODE);
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
