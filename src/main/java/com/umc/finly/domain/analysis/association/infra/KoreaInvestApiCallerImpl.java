package com.umc.finly.domain.analysis.association.infra;

import com.umc.finly.domain.analysis.association.dto.KoreaInvestRawResponse;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestErrorCode;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 한국투자증권 API 실제 구현체
 */
@Component
public class KoreaInvestApiCallerImpl implements DailyChartApiCaller {

    private final RestClient restClient;

    public KoreaInvestApiCallerImpl(
            @Qualifier("koreaInvestRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    // Daily Chart: 국내주식기간별시세(일/주/월/년)[v1_국내주식-016] -> 일봉 조회
    @Override
    public List<Map<String, Object>> fetchDailyCandles(String symbol, String startDate, String endDate) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // 주식 시장 분류 (J: KRX)
                        .queryParam("FID_INPUT_ISCD", symbol)       // 종목 코드
                        .queryParam("FID_INPUT_DATE_1", startDate)  // 시작일
                        .queryParam("FID_INPUT_DATE_2", endDate)    // 종료일
                        .queryParam("FID_PERIOD_DIV_CODE", "D")    // D: 일봉
                        .queryParam("FID_ORG_ADJ_PRC", "0")         // 0: 수정주가 적용
                        .build())
                .header("tr_id", "FHKST03010100") // 거래 ID
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new KoreaInvestException(KoreaInvestErrorCode.API_CALL_ERROR);
                })
                .body(KoreaInvestRawResponse.class)
                .getOutput2(); // RawResponse 내부의 리스트 추출
    }
}
