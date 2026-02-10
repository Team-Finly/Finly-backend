package com.umc.finly.domain.member.service;

import com.umc.finly.domain.auth.exception.code.AuthErrorCode;
import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReqDTO;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitResDTO;
import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import com.umc.finly.domain.member.exception.MemberErrorCode;
import com.umc.finly.domain.member.repository.MemberPersonaResultRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaTestSubmitServiceImpl implements PersonaTestSubmitService{
    private final PersonaScoringService personaScoringService;
    private final MemberPersonaResultRepository memberPersonaResultRepository;

    @Override
    public PersonaTestSubmitResDTO submit(String mode, Long memberId, PersonaTestSubmitReqDTO request){

        // 공용 채점/검증 로직 재사용
        Persona persona = personaScoringService.resolvePersona(request.getAnswers());

        // mode 분기
        if ("signup".equals(mode)) {
            // 회원가입 전 페르소나 테스트 결과 미리보기: 저장 안 됨
            return buildResponse(persona, false, null, null);
        }

        if ("retest".equals(mode)) {
            // 로그인 후 재테스트
            if (memberId == null){
                throw new CustomException(AuthErrorCode.UNAUTHORIZED);
            }

            MembersPersonasResult result =
                    memberPersonaResultRepository.findByMemberId(memberId)
                            .map(existing -> existing.updatePersona(persona))
                            .orElseGet(() -> MembersPersonasResult.create(memberId, persona));

            MembersPersonasResult saved =
                    memberPersonaResultRepository.saveAndFlush(result);

            return buildResponse(
                    persona,
                    true,
                    saved.getCreatedAt(),
                    saved.getUpdatedAt()
            );
        }

        throw new CustomException(MemberErrorCode.INVALID_PERSONA_MODE);
    }

    private PersonaTestSubmitResDTO buildResponse(
            Persona persona,
            boolean saved,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        return PersonaTestSubmitResDTO.builder()
                .personaType(persona.getPersonaType().toUiType())
                .saved(saved)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
