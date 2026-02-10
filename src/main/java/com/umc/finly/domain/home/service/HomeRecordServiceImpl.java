package com.umc.finly.domain.home.service;

import com.umc.finly.domain.home.converter.HomeRecordConverter;
import com.umc.finly.domain.home.dto.response.HomeRecordsResDTO;
import com.umc.finly.domain.record.dto.res.RecordSearchResDTO;
import com.umc.finly.domain.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeRecordServiceImpl implements HomeRecordService {

    private final RecordService recordService;

    @Override
    public HomeRecordsResDTO getRecentMyRecords(Long memberId) {

        // 기존 기록 검색 API 로직 재사용
        RecordSearchResDTO searchRes =
                recordService.searchRecords(memberId, null, null);

        // Home 정책 적용
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        List<HomeRecordsResDTO.Record> records =
                searchRes.getRecords().stream()
                        // 최근 3일
                        .filter(r -> !r.getRecordDate().isBefore(threeDaysAgo))
                        // 최신순
                        .sorted(Comparator.comparing(
                                RecordSearchResDTO.SearchEntry::getRecordedAt
                        ).reversed())
                        // 상위 5개
                        .limit(5)
                        // DTO 변환
                        .map(HomeRecordConverter::toHomeRecord)
                        .toList();

        return HomeRecordsResDTO.from(records);
    }
}
