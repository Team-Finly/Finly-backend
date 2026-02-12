package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.enums.PersonaUiType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PersonaTestSubmitResDTO {
    // 페르소나 제출 응답 DTO

    private final PersonaUiType personaType;
    private final boolean saved;
    private final LocalDateTime createdAt;    // 최초 페르소나 결과 생성 시각
    private final LocalDateTime updatedAt;    // 최근 페르소나 재테스트 결과 생성 시각
}
