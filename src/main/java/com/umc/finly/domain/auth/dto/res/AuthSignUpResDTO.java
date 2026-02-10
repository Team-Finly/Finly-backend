package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSignUpResDTO {
    private Long memberId;
    private String email;
    private String nickname;
    private Long personaId;
}
