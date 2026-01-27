package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.req.AuthSignUpReq;
import com.umc.finly.domain.auth.dto.res.AuthSignUpRes;

public interface AuthService {
    boolean isEmailAvailable(String email);

    AuthSignUpRes signup(AuthSignUpReq request);
}