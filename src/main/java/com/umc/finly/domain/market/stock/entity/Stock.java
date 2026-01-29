package com.umc.finly.domain.market.stock.entity;

import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock", uniqueConstraints = {
        @UniqueConstraint(name = "stock_symbol_unique",
                columnNames = "symbol")})
public class Stock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 시장 타입 (ex. KOSPI / KOSDAQ)
    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false)
    private MarketType marketType;

    // KRX 종목 코드 (ex. 000020)
    @Column(name = "symbol", nullable = false)
    private String symbol;

    // 종목명 (ex. 동화약품)
    @Column(name = "name", nullable = false)
    private String name;

    // 국제증권식별번호 (ex. KR7000020008)
    @Column(name = "isin", nullable = false)
    private String isin;

    // 현재 상장 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // 로고 이미지 url
    @Column(name = "logo_url")
    private String logoUrl;

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }
}