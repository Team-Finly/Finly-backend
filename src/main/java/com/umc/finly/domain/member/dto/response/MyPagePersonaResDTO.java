package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.enums.PersonaUiType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyPagePersonaResDTO {

    private PersonaUiType personaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
