package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.mapping.MembersPersonasResult;
import com.umc.finly.domain.member.enums.PersonaType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPagePersonaRes {

    private PersonaType personaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyPagePersonaRes from(MembersPersonasResult result){
        return MyPagePersonaRes.builder()
                .personaType(result.getPersona().getPersonaType())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}
