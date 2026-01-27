package com.umc.finly.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PersonaTestSubmitRes {
    // 최종 계산된 페르소나 정보

    private PersonaRes persona;
    private boolean saved;
    private LocalDateTime createdAt;    // 최초 페르소나 결과 생성 시각
    private LocalDateTime updatedAt;    // 최근 페르소나 재테스트 결과 생성 시각

    @Getter
    @Builder
    public static class PersonaRes {
        // 페르소나 상세 정보 응답 DTO

        private Long id;
        private String title;
        private String description;
        private String iconUrl;
    }
}
