package com.umc.finly.domain.auth.enums;

import lombok.Getter;

@Getter
public enum TermType {

    TERMS_AGREED(true),        // (필수) 이용약관 동의
    PRIVACY_AGREED(true),      // (필수) 개인정보처리방침 동의
    MARKETING_AGREED(false);   // (선택) 마케팅 정보 수신 동의

    private final boolean required;

    TermType(boolean required) {
        this.required = required;
    }
}
