package com.umc.finly.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthLoginResDTO {
    // 로그인 응답 DTO

    private String accessToken;
    private MemberInfo member;

    @Getter
    @AllArgsConstructor
    public static class MemberInfo {
        private Long memberId;
        private String email;
        private String nickname;
    }
}
