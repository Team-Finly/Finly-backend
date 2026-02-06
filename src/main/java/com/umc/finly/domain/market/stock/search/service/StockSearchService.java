package com.umc.finly.domain.market.stock.search.service;

import com.umc.finly.domain.market.stock.search.dto.StockSearchResDTO;
import org.springframework.data.domain.Pageable;

public interface StockSearchService {
    /**
     * 검색어를 포함하는 종목명을 검색합니다.
     *
     * @param keyword  검색어 (예: "삼성")
     * @param pageable 페이지 정보 (page, size, sort)
     * @return 검색 결과 (검색어, 총 개수, 종목 리스트, 페이지 정보)
     */
    StockSearchResDTO searchStocks(String keyword, Pageable pageable);
}
