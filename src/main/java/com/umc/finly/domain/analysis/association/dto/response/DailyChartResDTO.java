package com.umc.finly.domain.analysis.association.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyChartResDTO {
    private Long stockId;
    private String symbol;
    private String stockName;
    private String today;
    private String startDate;
    private String endDate;
    private List<DailyDataDto> dailyData;

    @Getter
    @Builder
    public static class DailyDataDto {
        private String date;
        private String day;
        private Integer closePrice;
        private Integer recordCount;
        private List<String> emotions;
        private String mainEmotion;
    }
}
