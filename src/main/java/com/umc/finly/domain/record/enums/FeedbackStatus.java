package com.umc.finly.domain.record.enums;

// AI 피드백 상태 enum
// 피드백 생성 과정의 상태를 나타냄
// 흐름: PENDING → GENERATING → COMPLETED 또는 FAILED
public enum FeedbackStatus {
    PENDING,    // 대기중 - 피드백 요청됨, 아직 생성 시작 안함
    GENERATING, // 생성중 - OpenAI API 호출 중
    COMPLETED,  // 완료 - 피드백 생성 성공
    FAILED      // 실패 - 피드백 생성 실패 (에러 메시지 저장됨)
}
