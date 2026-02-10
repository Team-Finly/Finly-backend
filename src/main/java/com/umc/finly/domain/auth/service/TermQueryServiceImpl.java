package com.umc.finly.domain.auth.service;

<<<<<<< Updated upstream
import com.umc.finly.domain.auth.converter.TermConverter;
import com.umc.finly.domain.auth.dto.res.TermDetailResDTO;
import com.umc.finly.domain.auth.dto.res.TermResDTO;
=======
import com.umc.finly.domain.auth.dto.response.TermDetailResDTO;
import com.umc.finly.domain.auth.dto.response.TermResDTO;
>>>>>>> Stashed changes
import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.auth.exception.code.AuthErrorCode;
import com.umc.finly.domain.auth.repository.TermRepository;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermQueryServiceImpl implements TermQueryService {

    private final TermRepository termRepository;

    /** 약관 조회  **/
    @Override
    public TermResDTO.TermList getTerms() {
        List<Term> terms = termRepository.findAllByOrderByIdAsc();
        return TermConverter.toTermList(terms);
    }

    /** 약관 상세 조회 **/
    @Override
    public TermDetailResDTO getTermDetail(Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.TERM_NOT_FOUND));

        return TermConverter.toTermDetailResDTO(term);
    }
}