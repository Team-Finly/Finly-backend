package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.response.FragmentSummaryResDTO;

public interface FragmentService {
    FragmentSummaryResDTO getFragmentSummary(Long memberId);
}
