package com.umc.finly.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateNicknameRes {

    private String nickname;

    public static UpdateNicknameRes of(String nickname){
        return UpdateNicknameRes.builder()
                .nickname(nickname)
                .build();
    }
}
