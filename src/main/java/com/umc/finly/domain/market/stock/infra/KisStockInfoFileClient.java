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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * KIS 종목정보 압축파일(.zip)을 다운로드하고 압축을 해제하여 MST 스트림을 반환하는 클라이언트.
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
     * 공통 다운로드 및 압축 해제 로직
     */
    private InputStream download(String url) {
        try {
            // 1. HTTP 요청 객체 생성 (Method: GET, Timeout: 1분)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMinutes(1)) // 파일 다운로드는 응답 시간이 길 수 있으므로 넉넉히 설정
                    .GET()
                    .build();

            // 2. ZIP 파일 바이트 배열로 다운로드 (동기 블로킹)
            HttpResponse<byte[]> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // 3. 응답 상태 코드 검증
            if (response.statusCode() != 200) {
                throw new StockInfoException(
                        StockInfoErrorCode.KIS_FILE_DOWNLOAD_FAILED,
                        String.format("KIS 파일 다운로드 실패 [상태코드: %d, URL: %s]", response.statusCode(), url)
                );
            }

            // 4. ZIP 압축 해제 및 MST 파일 추출
            // 응답받은 바이트 배열을 ZipInputStream으로 변환
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(response.body()))) {
                ZipEntry entry = zis.getNextEntry();

                if (entry == null) {
                    throw new StockInfoException(
                            StockInfoErrorCode.KIS_FILE_DOWNLOAD_FAILED,
                            "압축 파일 내에 데이터가 존재하지 않습니다. URL: " + url
                    );
                }

                // 압축 파일 안의 데이터를 메모리에 적재하여 반환 (파싱을 위해)
                // zis를 직접 반환하면 try-with-resources에 의해 닫히므로 readAllBytes 사용
                return new ByteArrayInputStream(zis.readAllBytes());
            }

        } catch (StockInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new StockInfoException(
                    StockInfoErrorCode.KIS_FILE_DOWNLOAD_FAILED,
                    "KIS 파일 처리 중 시스템 예외가 발생했습니다. URL: " + url,
                    e
            );
        }
    }
}