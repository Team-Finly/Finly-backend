package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AnalysisScheduler {

//    private final FearIndexService fearIndexService;
//    private final ConvictionScoreService convictionScoreService;
//    private final MemberRepository memberRepository;
//
//    private static final int PERIOD = 7;
//
//    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시
//    @Transactional
//    public void runDailyAnalysis() {
//        LocalDate endDate = LocalDate.now();
//        LocalDate startDate = endDate.minusDays(PERIOD);
//
//        memberRepository.findAll().forEach(member -> {
//            // 1. 하락장 공포지수 별도 분석
//            fearIndexService.calculateAndSave(member.getId(), startDate, endDate);
//
//            // 2. 매수 확신도 별도 분석
//            convictionScoreService.calculateAndSave(member.getId(), startDate, endDate);
//        });
//    }
}
