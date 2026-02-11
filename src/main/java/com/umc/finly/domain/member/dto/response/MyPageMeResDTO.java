package com.umc.finly.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageMeResDTO {
    // 마이 페이지 - 내 정보 조회 응답 DTO

    private Long memberId;
    private String email;
    private String nickname;
    private String profileImageUrl;
}
