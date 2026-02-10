package com.umc.finly.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageMeResDTO {

    private Long memberId;
    private String email;
    private String nickname;
    private String profileImageUrl;
}
