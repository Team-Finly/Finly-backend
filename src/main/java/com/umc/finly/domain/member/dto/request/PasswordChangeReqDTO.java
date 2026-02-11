package com.umc.finly.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeReqDTO {
    // 비밀번호 변경 요청 DTO

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
    private String newPasswordConfirm;
}
