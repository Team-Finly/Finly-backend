package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.req.AuthLoginReq;
import com.umc.finly.domain.auth.dto.req.AuthSignUpReq;
import com.umc.finly.domain.auth.dto.res.AuthLoginRes;
import com.umc.finly.domain.auth.dto.res.AuthSignUpRes;

public interface AuthService {
    boolean isEmailAvailable(String email);

    AuthSignUpRes signup(AuthSignUpReq request);

    LoginTokens login(AuthLoginReq reqeust);

    record LoginTokens(
            AuthLoginRes body,
            String refreshToken,
            long refreshMaxAgeSeconds
    ){}
}