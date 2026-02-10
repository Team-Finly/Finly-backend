package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// 약관 관련 API 응답 DTO
public class TermResDTO {

    // 약관 목록 응답 Wrapper
    @Getter
    @AllArgsConstructor
    public static class TermList{
        private final List<TermItem> terms;
    }

    // 단일 약관 정보 DTO
    @Getter
    @AllArgsConstructor
    public static class TermItem{
        private final Long termId;
        private final String title;       // 화면에 표시할 문구
        private final Boolean required;   // 필수/선택
        private final String type;        // enum
    }
}
