package com.umc.finly.domain.record.enums;

// 매매 타입 enum
// 투자 기록의 거래 유형을 나타냄
public enum TradeAction {
    BUY,   // 매수 - 주식 구매 (unitPrice, quantity 필수)
    SELL,  // 매도 - 주식 판매 (unitPrice, quantity 필수)
    WATCH  // 관망 - 거래 없이 지켜봄 (unitPrice, quantity 선택)
}
