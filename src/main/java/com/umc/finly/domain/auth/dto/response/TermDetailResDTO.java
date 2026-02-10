package com.umc.finly.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TermDetailResDTO {

    private Long termId;
    private String termType;
    private String title;
    private String content;
}
