package com.umc.finly.domain.market.stock.search.service;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.market.stock.search.converter.StockSearchConverter;
import com.umc.finly.domain.market.stock.search.dto.StockSearchResDTO;
import com.umc.finly.domain.market.stock.search.exception.StockSearchErrorCode;
import com.umc.finly.domain.market.stock.search.exception.StockSearchException;
import com.umc.finly.global.apiPayload.exception.CustomException;
import com.umc.finly.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockSearchServiceImpl implements StockSearchService {

    private final StockRepository stockRepository;
    private final StockSearchConverter stockSearchConverter;

    @Override
    public StockSearchResDTO searchStocks(String keyword, Pageable pageable) {
        try {
            String trimmedKeyword = validateKeyword(keyword);

            Page<Stock> stockPage = stockRepository.searchByName(trimmedKeyword, pageable);

            return stockSearchConverter.toStockSearchResponse(trimmedKeyword, stockPage);
        } catch (StockSearchException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "종목 검색 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    // 검색어 검증 메서드
    private String validateKeyword(String keyword) {

        if (keyword == null) {
            throw new StockSearchException(StockSearchErrorCode.EMPTY_KEYWORD);
        }

        String trimmed = keyword.strip();

        if (trimmed.isEmpty()) {
            throw new StockSearchException(StockSearchErrorCode.EMPTY_KEYWORD);
        }

        return trimmed;
    }
}
