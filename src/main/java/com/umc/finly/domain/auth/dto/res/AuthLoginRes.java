package com.umc.finly.domain.auth.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthLoginRes {
    // 로그인 응답 DTO

    private String accessToken;
    private MemberInfo member;

    @Getter
    @Builder
    public static class MemberInfo {
        private Long memberId;
        private String email;
        private String nickname;
    }
}
