package com.umc.finly.stock;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.market.stock.enums.MarketType;
import com.umc.finly.domain.market.stock.repository.StockRepository;
import com.umc.finly.domain.market.stock.service.StockInfoSyncService;
import com.umc.finly.global.config.security.SecurityConfig;
import com.umc.finly.global.infra.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
@Transactional
//@Rollback(false) // DB에 데이터 지우지 않고 남기기
class StockInfoSyncServiceTest {

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private SecurityConfig securityConfig;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StockInfoSyncService stockInfoSyncService;

    @Autowired
    private StockRepository stockRepository;

    // @MockitoBean을 사용하지 않고 실제 KisStockInfoFileClient 빈을 사용합니다.
    // 만약 Client 내부 URL이 @Value 등으로 주입된다면 application-test.yml에 URL이 설정되어 있어야 합니다.

    @Test
    @DisplayName("실제 KIS 서버에서 파일을 다운로드하여 전체 동기화 프로세스를 수행한다")
    void syncDomesticStocks_RealServer_Success() {
        // given: 사전 데이터가 없는 깨끗한 상태 혹은 기존 버전 존재 상태

        // when: 실제 네트워크 호출을 포함한 서비스 실행
        stockInfoSyncService.syncDomesticStocks();

        // then: DB 검증
        // 실제 데이터가 들어왔는지 확인
        List<Stock> allStocks = stockRepository.findAll();

        // 1. 최소한 종목들이 저장되었는지 확인
        assertThat(allStocks).isNotEmpty();
        System.out.println("Total Stocks Synced: " + allStocks.size());

        // 2. 특정 우량주가 데이터가 정확히 들어왔는지 확인 (예시: 삼성제약)
        String targetSymbol = "001360";
        String targetIsin = "KR7001360007";

        Stock samsungPharmaceutical = stockRepository.findBySymbol(targetSymbol).orElse(null);

        if (samsungPharmaceutical != null) {
            // 종목코드(Symbol) 검증
            assertThat(samsungPharmaceutical.getSymbol()).isEqualTo(targetSymbol);

            // 표준코드(ISIN) 검증
            assertThat(samsungPharmaceutical.getIsin()).isEqualTo(targetIsin);

            // 종목명 검증
            assertThat(samsungPharmaceutical.getName()).contains("삼성제약");

            // 시장 타입 검증
            assertThat(samsungPharmaceutical.getMarketType()).isEqualTo(MarketType.KOSPI);

            assertThat(samsungPharmaceutical.getIsActive()).isTrue();
        } else {
            // 만약 파일을 읽었는데 삼성제약이 없다면 테스트 실패 처리
            org.junit.jupiter.api.Assertions.fail("실제 데이터에 삼성제약(001360)이 존재하지 않습니다. 파일을 확인하세요.");
        }
    }

    @Test
    @DisplayName("DB 저장 결과 직접 출력 테스트")
    void verifyStockData() {
        // 1. 동기화 실행
        stockInfoSyncService.syncDomesticStocks();

        // 2. 현재 DB에 저장된 총 개수 가져오기
        long count = stockRepository.count();

        System.out.println("========================================");
        System.out.println(">>> ✅ 현재 DB에 저장된 총 종목 수: " + count);
        System.out.println("========================================");

        // 3. 데이터가 있다면 상위 10개 출력
        if (count > 0) {
            stockRepository.findAll().stream().limit(10).forEach(stock ->
                    System.out.printf("✅ 코드: %s | 이름: %s | ISIN: %s | 시장: %s%n",
                            stock.getSymbol(), stock.getName(), stock.getIsin(), stock.getMarketType())
            );
        }

        // 4. 검증
        assertThat(count).as("❗종목 데이터가 하나도 저장되지 않았습니다!").isGreaterThan(0);
    }
}