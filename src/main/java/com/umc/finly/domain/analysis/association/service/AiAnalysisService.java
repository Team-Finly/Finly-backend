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
    @Transactional(readOnly = true)
    public AiAnalysisResDTO getAiAnalysis(Long memberId) {
        log.info("통계-연관분석: AI 분석 시작 - Member ID: {}", memberId);

        FearIndexResult fear = fearIndexResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .orElseThrow(() -> new FearIndexException(FearIndexErrorCode.FEAR_INDEX_NOT_FOUND));

        ConvictionScoreResult conviction = convictionScoreResultRepository.findFirstByMemberIdOrderByEndDateDesc(memberId)
                .orElseThrow(() -> new ConvictionScoreException(ConvictionScoreErrorCode.CONVICTION_SCORE_NOT_FOUND));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        List<RecordEntry> recentRecords = recordEntryRepository.findAllByMemberIdAndRecordDateBetween(memberId, startDate, endDate);

        // 1. 프롬프트 생성
        String userPrompt = promptBuilder.buildGeneralAnalysisPrompt(fear, conviction, recentRecords);

        // 2. OpenAiFeedbackClient에 맞는 System Prompt 설정
        // Client가 JSON 파싱을 시도하므로, JSON 형식으로 응답하도록 강제해야 합니다.
        String systemPrompt = "당신은 주식 심리 및 투자 패턴 분석 전문가입니다. 반드시 다음 JSON 형식으로만 응답하세요: {\"content\": \"분석내용\", \"suggestion\": \"행동조언\"}";

        try {
            // 3. generateFeedback 호출
            OpenAiFeedbackClient.FeedbackResponse response = openAiFeedbackClient.generateFeedback(systemPrompt, userPrompt);

            // 4. Client가 파싱해준 content와 suggestion을 합쳐서 우리가 원하는 3줄 형식으로 반환
            return AiAnalysisResDTO.builder()
                    .text(response.content())
                    .build();

        } catch (Exception e) {
            log.error("통계-연관분석: AI 분석 중 오류 발생: {}", e.getMessage());
            return AiAnalysisResDTO.builder()
                    .text("🧠 현재 분석 서비스 이용량이 많아 결과 생성이 지연되고 있습니다.\n📉 잠시 후 다시 시도해 주시면 분석 결과를 제공해 드릴게요.\n💡 지속적인 감정 기록은 더 정확한 분석에 도움이 됩니다.")
                    .build();
        }
    }
}