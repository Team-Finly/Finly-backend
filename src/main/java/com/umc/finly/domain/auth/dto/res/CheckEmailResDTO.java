package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CheckEmailResDTO {

    private final boolean available;

    // 이메일 중복 확인
    public static CheckEmailResDTO of(boolean available){
        return CheckEmailResDTO.builder()
                .available(available)
                .build();
    }
}