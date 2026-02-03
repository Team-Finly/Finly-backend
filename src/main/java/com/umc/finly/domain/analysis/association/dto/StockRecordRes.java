package com.umc.finly.domain.analysis.association.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockRecordRes {

    private Integer stokId; // 종목 아이디
    private String stokCode;// 종목 코드
    private String stokName; // 종목 이름

}
