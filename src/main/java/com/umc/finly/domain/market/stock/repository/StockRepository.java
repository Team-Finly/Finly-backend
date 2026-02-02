package com.umc.finly.domain.market.stock.repository;

import com.umc.finly.domain.market.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    // symbol로 종목 조회
    Optional<Stock> findBySymbol(String symbol);
    // logoUrl이 null인 종목 조회
    List<Stock> findByLogoUrlIsNull();
    // 종목명에 검색어 포함한 종목 조회
    List<Stock> findByNameContaining(String keyword);
}
