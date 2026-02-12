package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.enums.PersonaUiType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyPagePersonaResDTO {
    // 마이 페이지 - 내 페르소나 조회 응답 DTO

    private PersonaUiType personaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
