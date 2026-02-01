package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.market.stock.service.StockLogoUpdateService;
import com.umc.finly.global.config.security.SecurityConfig;
import com.umc.finly.global.infra.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
@Transactional
public class StockLogoUpdateServiceTest {

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private SecurityConfig securityConfig;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StockLogoUpdateService stockLogoUpdateService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        Stock samsung = Stock.builder()
                .symbol("005930")
                .name("삼성전자")
                .isin("KR7005930003")
                .marketType(MarketType.KOSPI)
                .isActive(true)
                .logoUrl(null)
                .build();
        stockRepository.findBySymbol("005930").ifPresent(stockRepository::delete);
        stockRepository.save(samsung);
    }

    @Test
    //@Rollback(false)
    @DisplayName("로고 URL이 없는 종목들을 찾아 TradingView에서 로고를 업데이트한다")
    void updateMissingLogos_Success() {
        // given: 로고가 없는 종목 확인
        List<Stock> beforeTargets = stockRepository.findByLogoUrlIsNull();
        int initialSize = beforeTargets.size();
        System.out.println(">>> 업데이트 전 로고가 없는 종목 수: " + initialSize);

        // when: 로고 업데이트 서비스 실행
        stockLogoUpdateService.updateMissingLogos();

        // then: 업데이트 결과 확인
        List<Stock> afterTargets = stockRepository.findByLogoUrlIsNull();

        System.out.println(">>> 업데이트 후 로고가 없는 종목 수: " + afterTargets.size());

        // 특정 종목(삼성전자)을 뽑아서 URL이 정말 들어왔는지 확인
        stockRepository.findBySymbol("005930").ifPresent(stock -> {
            System.out.println(">>> 삼성전자 로고 URL: " + stock.getLogoUrl());
            // 성공했다면 null이 아니어야 함
            assertThat(stock.getLogoUrl()).isNotNull();
        });
    }
}
