package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthReissueRes {

    private String accessToken;

    public static AuthReissueRes of(String accessToken){
        return AuthReissueRes.builder()
                .accessToken(accessToken)
                .build();
    }
}
