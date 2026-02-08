package com.umc.finly.domain.record.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecentSearchResDTO {

    private List<String> recentKeywords;

    public static RecentSearchResDTO from(List<String> keywords) {
        return RecentSearchResDTO.builder()
                .recentKeywords(keywords)
                .build();
    }
}
