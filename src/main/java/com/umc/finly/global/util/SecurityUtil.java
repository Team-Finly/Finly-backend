package com.umc.finly.global.util;

import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof Long)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return (Long) principal;
    }
}
