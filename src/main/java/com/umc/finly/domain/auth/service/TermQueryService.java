package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.response.TermDetailResDTO;
import com.umc.finly.domain.auth.dto.response.TermResDTO;

public interface TermQueryService {
    TermResDTO.TermList getTerms();
    TermDetailResDTO getTermDetail(Long termId);
}
