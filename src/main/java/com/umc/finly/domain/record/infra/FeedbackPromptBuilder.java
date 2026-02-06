package com.umc.finly.domain.record.infra;

import com.umc.finly.domain.market.stock.entity.Stock;
import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedbackPromptBuilder {

    private static final String SYSTEM_PROMPT = """
            당신은 투자자의 감정과 행동 패턴을 분석하는 따뜻하고 공감적인 투자 심리 코치입니다.

            역할:
            - 투자자의 감정 상태를 이해하고 공감합니다
            - 과거 투자 패턴을 분석하여 인사이트를 제공합니다
            - 감정적 의사결정의 패턴을 파악하고 알려줍니다

            중요한 규칙:
            - 절대로 구체적인 투자 조언(매수/매도 추천, 목표가, 종목 추천 등)을 하지 마세요
            - 항상 따뜻하고 지지적인 톤을 유지하세요
            - content와 suggestion은 한 화면에 들어갈 분량(200-300자)으로 작성하세요
            - 비판보다는 인사이트와 자기 인식을 돕는 방향으로 작성하세요

            키워드 강조 규칙 (반드시 준수):
            - 감정 키워드는 {{중괄호}}로 감싸세요: {{불안}}, {{후회}}, {{확신}}, {{탐욕}}, {{평온}}
            - 수치/날짜 키워드는 <<꺾쇠괄호>>로 감싸세요: <<3일>>, <<15%>>, <<2건>>, <<평균 2.5>>
            - 예시: "삼성전자의 하락에 {{불안}}하셨군요. 최근 <<5건>> 중 <<3건>>이 {{불안}} 상태에서 매도하셨네요."

            응답 형식 (반드시 JSON으로 응답):
            {
              "content": "피드백 본문 (1~4번 항목을 자연스럽게 연결하여 작성)",
              "suggestion": 행동 제안 1, 행동 제안 2
            }

            content 구조 (1~4번을 자연스럽게 연결):
            1. 오늘 기록 요약 (1-2문장)
            2. 감정 공감 (1-2문장)
            3. 패턴 분석 - 과거 기록과 비교한 인사이트 (2-3문장)
            4. 현재 결정에 대한 통찰 (1-2문장)

            suggestion 구조:
            - 즉시 실행 가능한 행동 제안 2개를 작성
            - 각 제안은 반드시 구체적으로
            - 첫 번째 제안은 ~요 체로, 두 번째 제안은 ~하면 어떨까요? 체로 작성
            """;

    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    public String buildUserPrompt(RecordEntry currentEntry, Stock stock, List<RecordEntry> pastEntries) {
        StringBuilder sb = new StringBuilder();

        sb.append("## 오늘의 투자 기록\n");
        sb.append(formatCurrentEntry(currentEntry, stock));

        if (pastEntries != null && !pastEntries.isEmpty()) {
            sb.append("\n## 최근 투자 기록 요약 (최근 ").append(pastEntries.size()).append("건)\n");
            sb.append(formatPastEntries(pastEntries));
        }

        sb.append("\n위 정보를 바탕으로 투자자에게 도움이 될 피드백을 작성해주세요.");

        return sb.toString();
    }

    private String formatCurrentEntry(RecordEntry entry, Stock stock) {
        StringBuilder sb = new StringBuilder();

        String stockName = stock != null ? stock.getName() : "종목 ID: " + entry.getStockId();
        sb.append("- 종목: ").append(stockName).append("\n");
        sb.append("- 거래 유형: ").append(formatTradeAction(entry.getTradeAction().name())).append("\n");

        if (entry.getUnitPrice() != null) {
            sb.append("- 단가: ").append(entry.getUnitPrice()).append("원\n");
        }
        if (entry.getQuantity() != null) {
            sb.append("- 수량: ").append(entry.getQuantity()).append("주\n");
        }

        sb.append("- 감정: ").append(formatEmotion(entry.getEmotionCode().name())).append("\n");
        sb.append("- 감정 강도: ").append(entry.getEmotionIntensity()).append("/7\n");
        sb.append("- 시간대: ").append(formatSession(entry.getSession().name())).append("\n");

        if (entry.getMemo() != null && !entry.getMemo().isBlank()) {
            sb.append("- 메모: ").append(entry.getMemo()).append("\n");
        }

        return sb.toString();
    }

    private String formatPastEntries(List<RecordEntry> entries) {
        StringBuilder sb = new StringBuilder();

        // 감정 통계
        long calmCount = entries.stream().filter(e -> "CALM".equals(e.getEmotionCode().name())).count();
        long anxietyCount = entries.stream().filter(e -> "ANXIETY".equals(e.getEmotionCode().name())).count();
        long regretCount = entries.stream().filter(e -> "REGRET".equals(e.getEmotionCode().name())).count();
        long greedCount = entries.stream().filter(e -> "GREED".equals(e.getEmotionCode().name())).count();
        long confidenceCount = entries.stream().filter(e -> "CONFIDENCE".equals(e.getEmotionCode().name())).count();

        sb.append("### 감정 분포\n");
        sb.append("- 평온: ").append(calmCount).append("건\n");
        sb.append("- 불안: ").append(anxietyCount).append("건\n");
        sb.append("- 후회: ").append(regretCount).append("건\n");
        sb.append("- 탐욕: ").append(greedCount).append("건\n");
        sb.append("- 확신: ").append(confidenceCount).append("건\n");

        // 거래 유형 통계
        long buyCount = entries.stream().filter(e -> "BUY".equals(e.getTradeAction().name())).count();
        long sellCount = entries.stream().filter(e -> "SELL".equals(e.getTradeAction().name())).count();
        long watchCount = entries.stream().filter(e -> "WATCH".equals(e.getTradeAction().name())).count();

        sb.append("\n### 거래 유형 분포\n");
        sb.append("- 매수: ").append(buyCount).append("건\n");
        sb.append("- 매도: ").append(sellCount).append("건\n");
        sb.append("- 관망: ").append(watchCount).append("건\n");

        // 평균 감정 강도
        double avgIntensity = entries.stream()
                .mapToInt(RecordEntry::getEmotionIntensity)
                .average()
                .orElse(0.0);
        sb.append("\n### 평균 감정 강도: ").append(String.format("%.1f", avgIntensity)).append("/7\n");

        return sb.toString();
    }

    private String formatTradeAction(String action) {
        return switch (action) {
            case "BUY" -> "매수";
            case "SELL" -> "매도";
            case "WATCH" -> "관망";
            default -> action;
        };
    }

    private String formatEmotion(String emotion) {
        return switch (emotion) {
            case "CALM" -> "평온";
            case "ANXIETY" -> "불안";
            case "REGRET" -> "후회";
            case "GREED" -> "탐욕";
            case "CONFIDENCE" -> "확신";
            default -> emotion;
        };
    }

    private String formatSession(String session) {
        return switch (session) {
            case "PRE_MARKET" -> "장 시작 전";
            case "MARKET_OPEN" -> "장중";
            case "POST_MARKET" -> "장 마감 후";
            default -> session;
        };
    }
}
