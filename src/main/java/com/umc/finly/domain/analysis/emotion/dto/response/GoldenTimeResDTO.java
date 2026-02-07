package com.umc.finly.domain.analysis.emotion.dto.response;

import com.umc.finly.domain.record.enums.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldenTimeResDTO {

    private SelectedStock stock;
    private Summary summary;
    private List<Sessions> session;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedStock {
        private String symbol;
        private String name;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private int totalRecords;
        private Session goldenTime;
        private String goldenTimeName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sessions {
        private Session session;
        private String sessionName;
        private int recordCount;
        private int percent;
    }
}
