package com.umc.finly.domain.analysis.association.service;

import com.umc.finly.domain.analysis.association.dto.AiAnalysisResDTO;
import com.umc.finly.domain.analysis.association.entity.ConvictionScoreResult;
import com.umc.finly.domain.analysis.association.entity.FearIndexResult;
import com.umc.finly.domain.analysis.association.infra.AiAnalysisPromptBuilder;
import com.umc.finly.domain.analysis.association.repository.ConvictionScoreResultRepository;
import com.umc.finly.domain.analysis.association.repository.FearIndexResultRepository;
import com.umc.finly.domain.record.entity.RecordEntry;
import com.umc.finly.domain.record.infra.OpenAiFeedbackClient;
import com.umc.finly.domain.record.repository.RecordEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 사용자의 공포지수, 매수확신도, 최근 7일 감정을 종합하여 AI 패턴 분석 결과를 반환합니다.
     */
    public AiAnalysisResDTO getAiAnalysis(Long memberId) {

        String today = LocalDate.now().toString();
        String redisKey = "analysis_association_ai:member:" + memberId + ":" + today;

        // Redis 캐시 확인
        String cachedResult = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedResult != null) {
            log.info("[연관분석 AI] Redis 캐시 데이터 반환 - Member ID: {}, text: {}", memberId, cachedResult);
            return AiAnalysisResDTO.builder().text(cachedResult).build();
        }

        // 캐시 없으면 데이터 조회 및 AI 호출
        // 1. 데이터 조회
        AnalysisData data = getAnalysisData(memberId);
        log.info("[연관분석 AI] 데이터 로드 완료: 공포지수={}, 확신도={}, 기록수={}",
                data.fear() != null ? data.fear().getFearIndex() : "없음",
                data.conviction() != null ? data.conviction().getConvictionScore() : "없음",
                data.records() != null ? data.records().size() : 0);

        // 2. 프롬프트 구성
        String userPrompt = promptBuilder.buildGeneralAnalysisPrompt(data.fear(), data.conviction(), data.records());
        String systemPrompt = "당신은 주식 심리 및 투자 패턴 분석 전문가입니다. 반드시 JSON 형식으로만 응답하세요: {\"content\": \"심리/투자 패턴 분석\", \"suggestion\": \"행동 조언\"}";

        // 3. 외부 API 호출
        try {
            log.info("[연관분석 AI] OpenAI 호출: AI 분석 생성을 요청합니다.");
            OpenAiFeedbackClient.FeedbackResponse response = openAiFeedbackClient.generateFeedback(systemPrompt, userPrompt);

            String content = (response.content() != null) ? response.content().trim() : "";
            String suggestion = (response.suggestion() != null) ? response.suggestion().trim() : "";

            // 두 문장 사이에 공백이 있는지 확인하고 합침
            String combinedText = content.endsWith(".") ? content + " " + suggestion : content + ". " + suggestion;
            combinedText = combinedText.trim(); // 앞뒤 불필요 공백 제거

            log.info("[연관분석 AI] AI 분석 성공!: 텍스트 = {}", combinedText);

            // Redis 캐시 저장
            saveToCache(redisKey, combinedText);

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

    // Redis 캐시 저장 (자정마다 만료되어 삭제)
    private void saveToCache(String key, String text) {
        // 현재 시간과 다음 날 자정(00:00:00) 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
        // 2. 현재부터 자정까지 남은 시간(Duration) 계산
        Duration ttl = Duration.between(now, nextMidnight);
        // 3. Redis에 저장 (계산된 TTL 적용)
        stringRedisTemplate.opsForValue().set(key, text, ttl);

        log.info("[연관분석 AI] 캐시 저장 완료 (자정까지 남은 시간: {}분)", ttl.toMinutes());
    }

    // AI 분석 대상 결과 가져오기
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