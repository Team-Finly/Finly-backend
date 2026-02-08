package com.umc.finly.domain.analysis.association.infra;

import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.record.entity.RecordEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 통계 - 연관 분석 - AI 분석
 */
@Component
public class AiAnalysisPromptBuilder {

    public String buildGeneralAnalysisPrompt(FearIndexResult fear, ConvictionScoreResult conviction, List<RecordEntry> recentRecords) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 따뜻하고 예리한 주식 심리 패턴 및 투자 패턴 분석 전문가이자 멘토입니다. 아래 데이터를 분석하여 사용자에게 한 줄씩 분석 결과를 제공하세요.\n\n");
        sb.append("[데이터]\n");
        sb.append("- 공포지수: ").append(fear.getFearIndex()).append("/100\n");
        sb.append("- 매수확신도: ").append(conviction.getConvictionScore()).append("/100\n");
        sb.append("- 최근 감정: ").append(
                recentRecords.isEmpty() ? "기록 없음" :
                        recentRecords.stream().map(r -> r.getEmotionCode().name()).distinct().collect(Collectors.joining(", "))
        ).append("\n\n");

        sb.append("요구사항:\n");
        sb.append("1. 결과는 총 3줄로 작성할 것.\n");
        sb.append("2. 각 줄은 반드시 지정된 이모지 하나로만 시작할 것.\n");
        sb.append("3. '심리 패턴', '투자 패턴', '조언'이라는 단어를 절대 쓰지 말고 바로 본론만 작성할 것.\n");
        sb.append("4. 첫 번째 줄(🧠): 현재 심리 상태 분석\n");
        sb.append("5. 두 번째 줄(📉): 그에 따른 투자 행동 방식 분석\n");
        sb.append("6. 세 번째 줄(💡): 개선을 위한 핵심 조언\n");
        sb.append("7. 전체 문장은 5줄을 넘지 않으며, 대괄호[]나 특수기호를 쓰지 말 것.");
        sb.append("8. 응답은 반드시 JSON 형식으로 하되, 'content' 필드 안에 이모지로 시작하는 3줄 분석 내용을 넣고 'suggestion' 필드는 비워두세요.");
        sb.append("9. 어투는 부드럽고 친절해야 하고, 반드시 존댓말을 사용하세요. 이해하기 쉬운 단어를 사용하세요. 보고서 형식이 아니라, 실제 사람과 대화하는 것 같은 단어를 사용해야 합니다.");

        return sb.toString();
    }
}
