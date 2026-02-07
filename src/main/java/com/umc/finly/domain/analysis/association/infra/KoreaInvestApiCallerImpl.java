package com.umc.finly.domain.analysis.association.infra;

import com.umc.finly.domain.analysis.association.dto.KoreaInvestRawResponse;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestErrorCode;
import com.umc.finly.domain.analysis.association.exception.KoreaInvestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * 한국투자증권 API 실제 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KoreaInvestApiCallerImpl implements DailyChartApiCaller {

    private final RestClient koreaInvestRestClient;

    // Daily Chart: 국내주식기간별시세(일/주/월/년)[v1_국내주식-016] -> 일봉 조회
    @Override
    public List<Map<String, Object>> fetchDailyCandles(String symbol, String startDate, String endDate) {
        // API 호출
        KoreaInvestRawResponse response = koreaInvestRestClient.get()
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
                .onStatus(HttpStatusCode::isError, (request, response1) -> {
                    throw new KoreaInvestException(KoreaInvestErrorCode.API_CALL_ERROR);
                })
                .body(KoreaInvestRawResponse.class);

        validateResponse(response, symbol);

        return response.getOutput2();
    }

    private void validateResponse(KoreaInvestRawResponse response, String symbol) {
        // response가 null인 경우
        if (response == null) {
            log.error("❌ 한투 API 응답 바디가 null입니다. (역직렬화 실패 또는 빈 응답) - 종목코드: {}", symbol);
            throw new KoreaInvestException(KoreaInvestErrorCode.API_CALL_ERROR);
        }
        // 한투 응답 에러 (rt_cd != "0") 발생한 경우
        if (!"0".equals(response.getRtCd())) {
            log.error("❌ 한투 API 응답 에러(rt_cd=1) [종목코드: {}]: msg_cd={}, msg1={}",
                    symbol, response.getMsgCd(), response.getMsg1());
            throw new KoreaInvestException(KoreaInvestErrorCode.API_BUSINESS_ERROR);
        }
        // rt_cd는 0이지만 데이터(output2)가 없는 경우
        if (response.getOutput2() == null || response.getOutput2().isEmpty()) {
            log.warn("⚠\uFE0F 한투 API 데이터 유실 [종목코드: {}]: rt_cd는 성공이나 output2가 비어있음.", symbol);
            throw new KoreaInvestException(KoreaInvestErrorCode.NO_DATA_FOUND);
        }
    }
}
