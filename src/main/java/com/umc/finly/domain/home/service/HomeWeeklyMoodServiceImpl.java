package com.umc.finly.domain.home.service;

<<<<<<< Updated upstream
import com.umc.finly.domain.home.converter.HomeWeeklyMoodConverter;
import com.umc.finly.domain.home.dto.response.HomeWeeklyMoodResDTO;
import com.umc.finly.domain.home.exception.HomeErrorCode;
=======
import com.umc.finly.domain.home.dto.response.HomeWeeklyMoodResDTO;
import com.umc.finly.domain.home.exception.code.HomeErrorCode;
>>>>>>> Stashed changes
import com.umc.finly.domain.home.repository.HomeRecordRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeWeeklyMoodServiceImpl implements HomeWeeklyMoodService {

    private final HomeRecordRepository homeRecordRepository;

    @Override
    public HomeWeeklyMoodResDTO getWeeklyMood(Long memberId) {


        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);


        List<RecordEntry> records =
                homeRecordRepository.findByMemberIdAndRecordDateBetween(
                        //  //  ️이번 주 동안 사용자가 작성한 모든 기록 조회 recordDate 기준
                        memberId, monday, sunday
                ).orElseThrow(() ->
                        new CustomException(HomeErrorCode.HOME_WEEKLY_MOOD_INTERNAL_ERROR) // 에러 추가
                );

        //기록을 요일(DayOfWeek) 기준으로 그룹핑
        Map<DayOfWeek, List<RecordEntry>> grouped =
                records.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getRecordDate().getDayOfWeek()
                        ));

        //월요일 ~ 일요일 순서로 위클리 무드 응답 생성
        List<HomeWeeklyMoodResDTO.DayMood> days = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {

            List<RecordEntry> dayRecords =
                    grouped.getOrDefault(day, List.of());
            if (dayRecords.isEmpty()) {
                days.add(HomeWeeklyMoodConverter.toEmptyDayMood(day));
            } else {
                days.add(HomeWeeklyMoodConverter.toDayMood(day, dayRecords));
            }
        }

        return HomeWeeklyMoodResDTO.builder()
                .days(days)
                .build();
    }


}
