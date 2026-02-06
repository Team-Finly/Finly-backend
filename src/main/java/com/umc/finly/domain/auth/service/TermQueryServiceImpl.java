package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.res.TermDetailResDTO;
import com.umc.finly.domain.auth.dto.res.TermResDTO;
import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.exception.AuthErrorCode;
import com.umc.finly.domain.auth.repository.TermRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
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
    public TermResDTO.TermList getTerms(){
        List<Term> terms = termRepository.findAllByOrderByIdAsc();

        List<TermResDTO.TermItem> items = terms.stream()
                .map(t -> TermResDTO.TermItem.builder()
                        .termId(t.getId())
                        .title(t.getTitle())
                        .required(t.getTermType().isRequired())
                        .type(t.getTermType().name())
                        .build())
                .toList();
        return TermResDTO.TermList.builder()
                .terms(items)
                .build();
    }

    // 약관 상세 조회 -> 약관Id 로 조회
    @Override
    public TermDetailResDTO getTermDetail(Long termId){
        Term term = termRepository.findById(termId)
                .orElseThrow(()->new CustomException(AuthErrorCode.TERM_NOT_FOUND));

        return TermDetailResDTO.from(term);
    }
}
