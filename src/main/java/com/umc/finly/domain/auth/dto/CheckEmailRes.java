package com.umc.finly.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CheckEmailRes {

    private final boolean available;

    // 이메일 중복 확인
    public static CheckEmailRes of(boolean available){
        return CheckEmailRes.builder()
                .available(available)
                .build();
    }
}