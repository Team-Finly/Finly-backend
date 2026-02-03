package com.umc.finly.domain.analysis.association.repository;

import com.umc.finly.domain.analysis.association.dto.StockRecordRes;
import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AssociationAnalysisRepository extends  JpaRepository<RecordEntry,Long>{

   @Query("""
        select distinct new com.umc.finly.domain.analysis.association.dto.StockRecordRes(
            s.id,
            s.symbol,
            s.name
        )
        from RecordEntry r
        join Stock s on r.stockId = s.id
        where r.memberId = :memberId
    """)

   //사용자가 기록한 종목 조회
   List<StockRecordRes> findRecordedStocks(@Param("memberId") Long memberId);

}
