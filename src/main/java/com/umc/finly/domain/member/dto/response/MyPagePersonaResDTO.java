package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import com.umc.finly.domain.member.enums.PersonaUiType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPagePersonaResDTO {

    private PersonaUiType personaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyPagePersonaResDTO from(MembersPersonasResult result){
        return MyPagePersonaResDTO.builder()
                .personaType(result.getPersona().getPersonaType().toUiType())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}
