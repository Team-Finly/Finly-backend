package com.umc.finly.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TermDetailResDTO {
    // 약관 상세 조회 응답 DTO

    private Long termId;
    private String termType;
    private String title;
    private String content;
}
