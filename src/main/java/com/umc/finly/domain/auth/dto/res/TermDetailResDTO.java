package com.umc.finly.domain.auth.dto.res;

import com.umc.finly.domain.auth.entity.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TermDetailResDTO {

    private Long termId;
    private String termType;
    private String title;
    private String content;

    public static TermDetailResDTO from(Term term){
        return TermDetailResDTO.builder()
                .termId(term.getId())
                .termType(term.getTermType().name())
                .title(term.getTitle())
                .content(term.getContent())
                .build();
    }
}
