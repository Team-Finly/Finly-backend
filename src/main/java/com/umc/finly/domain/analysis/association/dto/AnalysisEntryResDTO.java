package com.umc.finly.domain.analysis.association.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalysisEntryResDTO {

    private RecordLevel recordLevel;
    private long totalRecordCount;
    private DefaultStockDto defaultStock;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DefaultStockDto {
        private Long stockId;
        private String symbol;
        private String name;
    }

    public enum RecordLevel {
        NONE, // totalRecordCount == 0
        LOW, // totalRecordCount == 1 or 2
        HIGH; // totalRecordCount >= 3

        public static RecordLevel fromRecordCount(long recordCount) {
            if (recordCount == 0) {
                return NONE;
            }
            if (recordCount <= 2) {
                return LOW;
            }
            return HIGH;
        }
    }
}
