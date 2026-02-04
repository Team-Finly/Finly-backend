package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignUpResDTO {
    private Long memberId;
    private String email;
    private String nickname;
    private Long personaId;
}
