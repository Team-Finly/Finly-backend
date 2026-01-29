package com.umc.finly.domain.market.stock.infra;

import com.umc.finly.domain.market.stock.exception.StockInfoErrorCode;
import com.umc.finly.domain.market.stock.exception.StockInfoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * KIS 종목정보파일을 HTTP로 다운로드하는 클라이언트.
 * 코스피/코스닥 각각 URL을 설정 파일에서 주입받는다.
 */
@Component
@RequiredArgsConstructor
public class KisStockInfoFileClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)) // 연결 타임아웃 설정
            .build();

    @Value("${kis.info.kospi-url}")
    private String kospiUrl;

    @Value("${kis.info.kosdaq-url}")
    private String kosdaqUrl;

    public InputStream downloadKospi() {
        return download(kospiUrl);
    }

    public InputStream downloadKosdaq() {
        return download(kosdaqUrl);
    }

    /**
     * 공통 다운로드 로직
     */
    private InputStream download(String url) {
        try {
            // 1. HTTP 요청 객체 생성 (Method: GET, Timeout: 1분)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMinutes(1)) // 파일 다운로드는 응답 시간이 길 수 있으므로 넉넉히 설정
                    .GET()
                    .build();

            // 2. 동기 블로킹 방식으로 데이터 다운로드 시도
            // BodyHandlers.ofByteArray()를 사용하여 바이트 배열로 응답 수신
            HttpResponse<byte[]> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // 3. HTTP 상태 코드 확인 (200 OK가 아니면 에러 발생)
            if (response.statusCode() != 200) {
                throw new StockInfoException(
                        StockInfoErrorCode.KIS_FILE_DOWNLOAD_FAILED,
                        String.format("KIS 파일 다운로드 실패 [상태코드: %d, URL: %s]", response.statusCode(), url)
                );
            }

            // 4. 다운로드 된 바이트 배열을 InputStream으로 변환하여 반환
            // 파싱 로직에서 메모리 효율을 위해 스트림 형태로 전달함
            return new ByteArrayInputStream(response.body());

        } catch (StockInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new StockInfoException(
                    StockInfoErrorCode.KIS_FILE_DOWNLOAD_FAILED,
                    "KIS 파일 다운로드 중 시스템 예외가 발생했습니다. URL: " + url
            );
        }
    }
}
