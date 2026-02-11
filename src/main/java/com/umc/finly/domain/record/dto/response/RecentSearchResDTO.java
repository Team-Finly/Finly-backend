package com.umc.finly.domain.record.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 최근 검색어 응답 DTO
// 사용자의 최근 검색 키워드 리스트 반환 (최대 3개)
@Getter
@Builder
public class RecentSearchResDTO {

    private List<String> recentKeywords; // 최근 검색 키워드 리스트 (최신순)
}
