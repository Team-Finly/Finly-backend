package com.umc.finly.domain.member.dto.response;

import com.umc.finly.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyPageMeResDTO {

    private Long memberId;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public static MyPageMeResDTO from(Member member){
        return MyPageMeResDTO.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
