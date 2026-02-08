package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.AiAnalysisResDTO;
import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.analysis.association.exception.ConvictionScoreErrorCode;
import com.umc.finly.domain.analysis.association.exception.ConvictionScoreException;
import com.umc.finly.domain.analysis.association.exception.FearIndexErrorCode;
import com.umc.finly.domain.analysis.association.exception.FearIndexException;
import com.umc.finly.domain.analysis.association.infra.AiAnalysisPromptBuilder;
import com.umc.finly.domain.analysis.association.repository.ConvictionScoreResultRepository;
import com.umc.finly.domain.analysis.association.repository.FearIndexResultRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.infra.OpenAiFeedbackClient;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final FearIndexResultRepository fearIndexResultRepository;
    private final ConvictionScoreResultRepository convictionScoreResultRepository;
    private final RecordEntryRepository recordEntryRepository;
    private final AiAnalysisPromptBuilder promptBuilder;
    private final OpenAiFeedbackClient openAiFeedbackClient;

    /**
     * 사용자의 공포지수, 매수확신도, 최근 7일 감정을 종합하여 AI 패턴 분석 결과를 반환합니다.
     */
    public AiAnalysisResDTO getAiAnalysis(Long memberId) {
        log.info("연관분석 AI: 분석 시작 - Member ID: {}", memberId);

        // 1. 데이터 조회
        AnalysisData data = getAnalysisData(memberId);
        log.info("[연관분석 AI] 데이터 로드 완료: 공포지수={}, 확신도={}, 기록수={}",
                data.fear() != null ? data.fear().getFearIndex() : "없음",
                data.conviction() != null ? data.conviction().getConvictionScore() : "없음",
                data.records() != null ? data.records().size() : 0);

        // 2. 프롬프트 구성
        String userPrompt = promptBuilder.buildGeneralAnalysisPrompt(data.fear(), data.conviction(), data.records());
        String systemPrompt = "당신은 주식 심리 및 투자 패턴 분석 전문가입니다. 반드시 JSON 형식으로만 응답하세요: {\"content\": \"심리/투자 분석\", \"suggestion\": \"행동 조언\"}";

        // 3. 외부 API 호출
        try {
            log.info("[연관분석 AI] OpenAI 호출: AI 분석 생성을 요청합니다.");
            OpenAiFeedbackClient.FeedbackResponse response = openAiFeedbackClient.generateFeedback(systemPrompt, userPrompt);

            String content = response.content() != null ? response.content() : "";
            String suggestion = response.suggestion() != null ? response.suggestion() : "";
            String combinedText = content + suggestion;

            log.info("[연관분석 AI] AI 분석 성공!: 텍스트 = {}", combinedText);

            return AiAnalysisResDTO.builder()
                    .text(combinedText)
                    .build();

        } catch (Exception e) {
            log.error("[연관분석 AI] AI 분석 중 오류 발생!: {}", e.getMessage());
            return AiAnalysisResDTO.builder()
                    .text("🧠 현재 분석 서비스 이용량이 많아 결과 생성이 지연되고 있어요.\n📉 잠시 후 다시 시도해 주시면 분석 결과를 제공해 드릴게요.\n💡 지속적인 감정 기록은 더 정확한 분석에 도움이 됩니다.")
                    .build();
        }
    }


    @Transactional(readOnly = true)
    protected AnalysisData getAnalysisData(Long memberId) {
        FearIndexResult fear = fearIndexResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .orElse(null);

        ConvictionScoreResult conviction = convictionScoreResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .orElse(null);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        List<RecordEntry> recentRecords = recordEntryRepository.findAllByMemberIdAndRecordDateBetween(memberId, startDate, endDate);

        return new AnalysisData(fear, conviction, recentRecords);
    }

    private record AnalysisData(
            FearIndexResult fear,
            ConvictionScoreResult conviction,
            List<RecordEntry> records
    ) {}
}