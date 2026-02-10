package com.umc.finly.domain.auth.dto.res;

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
