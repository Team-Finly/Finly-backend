package com.umc.finly.domain.analysis.association.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 한국투자증권 API 원본 응답 DTO
 */

@Getter
@NoArgsConstructor
public class KoreaInvestRawResponse {

    // 성공 실패 여부 (0: 성공)
    @JsonProperty("rt_cd")
    private String rtCd;

    // 응답 코드
    @JsonProperty("msg_cd")
    private String msgCd;

    // 응답 메시지
    @JsonProperty("msg1")
    private String msg1;

    // 응답 상세 (output2)
    @JsonProperty("output2")
    private List<Map<String, Object>> output2;
}
