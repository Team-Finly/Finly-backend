package com.umc.finly.domain.analysis.association.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalysisEntryResDTO {

    private RecordLevel recordLevel;
    private int recordCount;
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
        NONE, // recordCount == 0
        LOW, // recordCount == 1 or 2
        HIGH; // recordCount >= 3

        public static RecordLevel fromRecordCount(int recordCount) {
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
