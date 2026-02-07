package com.umc.finly.domain.analysis.association.repository;

import com.umc.finly.domain.analysis.association.dto.AnalysisStockResDTO;
import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnalysisStockRepository
        extends JpaRepository<RecordEntry, Long> {

    @Query("""
        select distinct new com.umc.finly.domain.analysis.association.dto.AnalysisStockResDTO(
            s.id,
            s.symbol,
            s.name
        )
        from RecordEntry r
        left join com.umc.finly.domain.market.stock.entity.Stock s
               on r.stockId = s.id
        where r.memberId = :memberId
          and s.id is not null
    """)
    List<AnalysisStockResDTO> findRecordedStocks(
            @Param("memberId") Long memberId
    );
}
