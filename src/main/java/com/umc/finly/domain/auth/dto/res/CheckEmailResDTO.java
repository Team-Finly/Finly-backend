package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckEmailResDTO {
    // 이메일 중복 확인 응답 DTO

    private final boolean available;
}