package com.umc.finly.global.security;

import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체 자체가 없거나 인증 안 된 경우
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 익명 사용자 방어 (anonymousUser도 isAuthenticated=true일 수 있음)
        if (auth instanceof AnonymousAuthenticationToken) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // AuthPrincipal을 principal로 세팅하는 경우
        if (principal instanceof AuthPrincipal ap) {
            // AuthPrincipal이 record면 ap.memberId()
            // class + getter면 ap.getMemberId()
            return ap.getMemberId();
        }

        // Long으로 세팅하는 경우도 지원
        if (principal instanceof Long memberId) {
            return memberId;
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
