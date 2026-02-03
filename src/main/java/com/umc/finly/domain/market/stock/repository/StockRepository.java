package com.umc.finly.domain.market.stock.repository;

import com.umc.finly.domain.market.stock.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    // symbol로 종목 조회
    Optional<Stock> findBySymbol(String symbol);
    // logoUrl이 null인 종목 조회
    List<Stock> findByLogoUrlIsNull();
    // 종목명에 검색어 포함한 종목 조회
    Page<Stock> findByNameContaining(String keyword, Pageable pageable);
    // 종목명에 검색어 포함한 종목 조회(앞에 포함 -> 끝에 포함 -> 중간에 포함 순으로 정렬)
    @Query("""
        select s
        from Stock s
        where s.name like concat('%', :keyword, '%')
        order by
            case
                when s.name like concat(:keyword, '%') then 0     
                when s.name like concat('%', :keyword) then 1     
                else 2                                           
            end,
            s.name asc
        """)
    Page<Stock> searchByName(@Param("keyword") String keyword, Pageable pageable);
}
