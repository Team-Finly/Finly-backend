package com.umc.finly.domain.analysis.association.infra;

import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 통계 - 연관 분석 - AI 분석
 */
@Component
public class AiAnalysisPromptBuilder {

    public String buildGeneralAnalysisPrompt(FearIndexResult fear, ConvictionScoreResult conviction, List<RecordEntry> recentRecords) {
        StringBuilder sb = new StringBuilder();

        sb.append("[분석 데이터]\n");
        sb.append("- 공포지수: ").append(fear != null ? fear.getFearIndex() + "/100" : "데이터 없음").append("\n");
        sb.append("- 매수확신도: ").append(conviction != null ? conviction.getConvictionScore() + "/100" : "데이터 없음").append("\n");
        sb.append("- 최근 감정: ").append(
                (recentRecords == null || recentRecords.isEmpty()) ? "기록 없음" :
                        recentRecords.stream()
                        .map(RecordEntry::getEmotionCode)
                        .filter(Objects::nonNull)
                        .map(Enum::name)
                        .distinct()
                        .collect(Collectors.joining(", "))
        ).append("\n\n");

        sb.append("요구사항:\n");
        sb.append("반드시 다음 JSON 형식을 지키세요: {\"content\": \"...\", \"suggestion\": \"...\"}\n");
        sb.append("'심리 패턴', '투자 행동 패턴', '조언'이라는 단어를 절대 쓰지 말고 바로 본론만 작성할 것.\n");
        sb.append("content 필드 구성:\n");
        sb.append("   - 현재 심리 상태 분석 한 문장과 투자 행동 및 패턴 분석 한 문장을 이어서 작성하세요.\n");
        sb.append("suggestion 필드 구성:\n");
        sb.append("   - 개선을 위한 핵심 조언 한 문장을 작성하세요.\n");
        sb.append("금지: 줄바꿈(\\n)은 절대 넣지 말고, 모든 문장은 공백 한 칸으로만 구분하세요.");
        sb.append("전체 문장은 3줄을 넘지 않으며, 대괄호[]나 특수기호를 쓰지 말 것.");
        sb.append("문장 마지막에는 반드시 마침표를 붙이세요.");
        sb.append("'사용자', '투자자' 지칭 금지, 반드시 존댓말을 사용하세요.\n");
        sb.append("부드럽고 친절한 어투를 사용하세요. 이해하기 쉬운 단어를 사용하세요.");

        return sb.toString();
    }
}