package com.umc.finly.domain.analysis.emotion.util;

import java.text.Normalizer;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 메모(자유형 텍스트)에서 토큰을 추출하는 유틸
public class KeywordExtractor {

    private static final Pattern TOKEN =
            Pattern.compile("([0-9A-Za-z가-힣]{2,})");

    // 의미 없는 단어(불용어) 목록
    private static final Set<String> STOPWORDS = Set.of(
            "오늘","어제","지금","진짜","너무","그냥","근데","그리고",
            "매수","매도","주식","종목",
            // 자주 튀어나오는 일반 동사/표현들 추가
            "보고","생각","생각했는데","느낌이","좋겠다","싶었다",
            "된다","하면","오면","와도","이후","때문에"
    );

    // 제거할 조사/어미(간단 룰)
    private static final String[] KOREAN_SUFFIXES = {
            "때문에",
            "했지만","했는데","했다",
            "이라","에서","에게",
            "으로","로",
            "까지","부터",
            "만","도",
            "은","는","이","가",
            "을","를",
            "에","와","과"
    };

    private KeywordExtractor() {
        // 유틸성 클래스이므로 인스턴스화를 방지
    }

    // DF(Document Frequency)용: 한 메모에서 중복 제거된 토큰 Set 반환
    public static Set<String> extractUniqueTokens(String text) {
        // 입력이 비어있으면 빈 Set 반환
        if (text == null || text.isBlank()) return Set.of();

        String s = normalize(text);
        Matcher m = TOKEN.matcher(s);

        Set<String> result = new HashSet<>();

        while (m.find()) {
            String token = m.group(1);

            // 조사/어미 제거 등 후처리
            token = postProcess(token);

            if (isValid(token)) {
                result.add(token);
            }
        }
        return result;
    }

    // 정규화를 통해 비교 가능한 형태로 변환
    private static String normalize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT);

        // 단어 경계를 만들기 위해 허용하지 않는 문자는 공백으로 치환
        return n.replaceAll("[^0-9a-zA-Z가-힣]+", " ");
    }

    // 토큰 후처리(간단 한국어 조사/어미 제거)
    private static String postProcess(String token) {
        if (token == null) return "";

        String t = token;

        // suffix들을 1회 제거(너무 과하면 의미가 깨질 수 있어서 1회만)
        for (String suf : KOREAN_SUFFIXES) {
            if (t.endsWith(suf) && t.length() > suf.length() + 1) {
                t = t.substring(0, t.length() - suf.length());
                break;
            }
        }
        return t;
    }

    // 토큰 유효성 검사
    private static boolean isValid(String t) {
        if (t == null) return false;
        if (t.length() < 2) return false;
        if (t.matches("\\d+")) return false;
        return !STOPWORDS.contains(t);
    }
}