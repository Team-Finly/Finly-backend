package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyPageMeRes {

    private Long memberId;
    private String email;
    private String nickname;

    public static MyPageMeRes from(Member member){
        return MyPageMeRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
