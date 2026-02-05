package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.req.AuthLoginReqDTO;
import com.umc.finly.domain.auth.dto.req.AuthSignUpReqDTO;
import com.umc.finly.domain.auth.dto.res.AuthLoginResDTO;
import com.umc.finly.domain.auth.dto.res.AuthSignUpResDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    boolean isEmailAvailable(String email);

    AuthSignUpResDTO signup(AuthSignUpReqDTO request);

    LoginTokens login(AuthLoginReqDTO request);

    ReissueTokens reissue(String refreshToken);

    void logout(HttpServletResponse response);

    record LoginTokens(
            AuthLoginResDTO body,
            String refreshToken,
            long refreshMaxAgeSeconds
    ){}

    record ReissueTokens(
            String accessToken,
            String refreshToken,
            long refreshMaxAgeSeconds
    ) {}
}