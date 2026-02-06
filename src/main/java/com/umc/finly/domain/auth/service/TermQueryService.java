package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.res.TermDetailResDTO;
import com.umc.finly.domain.auth.dto.res.TermResDTO;

public interface TermQueryService {
    TermResDTO.TermList getTerms();
    TermDetailResDTO getTermDetail(Long termId);
}
