package com.umc.finly.domain.analysis.association.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class MarketDataScheduler {

    //@Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    @Transactional
    public void collectMinuteChangeRate() {
        // TODO: 1. 외부 API 호출하여 전일 대비율 (MarketChangeRateHistory) 수집
        // TODO: 2. 하락 세션(DownSession) 판별 로직 실행 및 저장
    }

    //@Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    @Transactional
    public void collectDailyIndex() {
        // TODO: 1. 외부 API 호출하여 코스피/코스닥 일별 지수 (MarketIndexHistory) 수집
    }
}