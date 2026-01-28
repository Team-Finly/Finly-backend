package com.umc.finly.domain.auth.service;

import com.umc.finly.domain.auth.dto.res.TermRes;

public interface TermQueryService {
    TermRes.TermList getTerms();
}
