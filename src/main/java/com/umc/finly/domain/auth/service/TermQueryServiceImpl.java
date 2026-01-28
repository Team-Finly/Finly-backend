package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.res.TermRes;
import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 약관 조회 전용 Service
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermQueryServiceImpl implements TermQueryService{

    private final TermRepository termRepository;

    // 약관 목록 조회
    @Override
    public TermRes.TermList getTerms(){
        List<Term> terms = termRepository.findAllByOrderByIdAsc();

        List<TermRes.TermItem> items = terms.stream()
                .map(t -> TermRes.TermItem.builder()
                        .termId(t.getId())
                        .title(t.getTitle())
                        .required(t.getTermType().isRequired())
                        .type(t.getTermType().name())
                        .build())
                .toList();
        return TermRes.TermList.builder()
                .terms(items)
                .build();
    }
}
