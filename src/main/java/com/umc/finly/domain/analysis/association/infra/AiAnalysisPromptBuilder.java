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
        sb.append("반드시 한국어만 사용할 것.\n");
        sb.append("'심리 상태', '투자 행동 패턴', '조언'이라는 단어를 절대 쓰지 말고 바로 본론만 작성할 것.\n");
        sb.append("content 필드 구성:\n");
        sb.append("   - 심리 상태 분석 한 문장과 투자 행동 패턴 분석 한 문장을 작성하세요.\n");
        sb.append("감정을 언급할 경우, GREED는 '탐욕', CONFIDENCE는 '확신', REGRET는 '후회', ANXIETY는 '불안', CALM은 '평온'으로 매핑하여 출력하세요.\n");
        sb.append("suggestion 필드 구성:\n");
        sb.append("   - 개선을 위한 핵심 조언 한 문장을 작성하세요.\n");
        sb.append("금지: 줄바꿈(\\n)은 절대 넣지 말고, 모든 문장은 공백 한 칸으로만 구분하세요.\n");
        sb.append("전체 문장은 3줄을 넘지 않으며, 대괄호[]나 특수기호를 쓰지 말 것.\n");
        sb.append("문장 마지막에는 반드시 마침표를 붙이세요.\n");
        sb.append("'사용자', '투자자' 지칭 금지, 반드시 존댓말을 사용하세요.\n");
        sb.append("당신은 친근하고 다정한 금융 상담가입니다.\n");
        sb.append("딱딱한 '~습니다' 체 대신, 이웃과 대화하듯 자연스럽고 부드러운 '~요', '~해요', '~네요' 체를 사용하세요.\n");
        sb.append("문장 끝이 너무 반복되지 않도록 '~인 것 같아요', '~이 좋겠네요' 처럼 다양한 종결 어미를 섞어주세요.\n");
        sb.append("군더더기 없는 문어체보다는 대화하듯 매끄러운 구어체를 지향하세요.\n");
        sb.append("## 말투 가이드라인 (예시):\n");
        sb.append("변경 전: 현재 공포지수가 낮고 매수확신도가 높아 탐욕과 확신이 느껴집니다. 이러한 긍정적인 감정은 매수 활동을 촉진할 수 있지만, 지나친 탐욕은 위험을 초래할 수 있습니다. 안정성을 위해 과도한 감정에 휘둘리지 않고 신중한 결정을 내리는 것이 좋습니다.\n");
        sb.append("변경 후: 현재 공포지수가 낮고 매수확신도가 높아 탐욕과 학신이 느껴지네요. 이러한 긍정적인 감정은 매수 활동을 촉진할 수 있지만, 지나친 탐욕은 위험을 초래할 수 있어요. 안정성을 위해 과도한 감정에 휘둘리지 않고 신중한 결정을 내리는 것이 좋아요.\n");

        return sb.toString();
    }
}