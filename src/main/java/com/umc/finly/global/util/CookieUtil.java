package com.umc.finly.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    // 운영 환경에서 Secure=true 권장 (HTTPS)
    public void addRefreshTokenCookie(HttpServletResponse response,
                                      String refreshToken,
                                      long maxAgeSeconds,
                                      boolean secure){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(secure)
                .path("/api/auth")
                .sameSite("Lax")
                .maxAge(maxAgeSeconds)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
