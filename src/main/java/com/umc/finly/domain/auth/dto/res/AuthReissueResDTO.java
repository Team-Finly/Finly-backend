package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthReissueResDTO {

    private String accessToken;

    public static AuthReissueResDTO of(String accessToken){
        return AuthReissueResDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}
