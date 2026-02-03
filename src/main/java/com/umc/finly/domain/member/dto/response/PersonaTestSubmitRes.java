package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.enums.PersonaUiType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PersonaTestSubmitRes {
    private PersonaUiType personaType;
    private boolean saved;
    private LocalDateTime createdAt;    // 최초 페르소나 결과 생성 시각
    private LocalDateTime updatedAt;    // 최근 페르소나 재테스트 결과 생성 시각
}
