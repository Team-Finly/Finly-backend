package com.umc.finly.domain.auth.dto.res;

import com.umc.finly.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthLoginRes {
    // 로그인 응답 DTO

    private String accessToken;
    private Member member;

    @Getter
    @Builder
    public static class Member {
        private Long memberId;
        private String email;
        private String nickname;
    }
}
