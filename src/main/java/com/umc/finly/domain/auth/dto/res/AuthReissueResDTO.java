package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthReissueResDTO {
    // 토큰 재발급 응답 DTO

    private String accessToken;
}
