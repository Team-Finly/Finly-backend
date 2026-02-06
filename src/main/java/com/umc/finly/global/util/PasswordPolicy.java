package com.umc.finly.global.util;

public final class PasswordPolicy {
    private PasswordPolicy(){}

    // 비밀번호 정책: 영문 1개 이상 + 숫자 1개 이상 + 6자 이상
    public static final String REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";

    public static boolean isValid(String raw) {
        return raw != null && raw.matches(REGEX);
    }
}
