package com.umc.finly.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecentSearchRes {

    private List<String> recentKeywords;

    public static RecentSearchRes from(List<String> keywords) {
        return RecentSearchRes.builder()
                .recentKeywords(keywords)
                .build();
    }
}
