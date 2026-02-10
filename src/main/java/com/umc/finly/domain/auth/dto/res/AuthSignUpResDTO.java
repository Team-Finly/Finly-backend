package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSignUpResDTO {
    // 회원가입 응답 DTO

    private Long memberId;
    private String email;
    private String nickname;
    private Long personaId;
}
