package com.umc.finly.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthPrincipal {
    private final Long memberId;
    private final String email;
}
