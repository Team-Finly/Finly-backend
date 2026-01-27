package com.umc.finly.domain.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 약관 관련 API 응답 DTO
public class TermRes {

    // 약관 목록 응답 Wrapper
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermList{
        private List<TermItem> terms;
    }

    // 단일 약관 정보 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermItem{
        private Long termId;
        private String title;       // 화면에 표시할 문구
        private Boolean required;   // 필수/선택
        private String type;        // enum
    }
}
