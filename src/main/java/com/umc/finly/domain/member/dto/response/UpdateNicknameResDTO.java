package com.umc.finly.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateNicknameResDTO {

    private String nickname;

    public static UpdateNicknameResDTO of(String nickname){
        return UpdateNicknameResDTO.builder()
                .nickname(nickname)
                .build();
    }
}
