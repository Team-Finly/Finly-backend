package com.umc.finly.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateNicknameReqDTO {
    // 닉네임 변경 요청 DTO

    @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
    @Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
    private String nickname;
}
