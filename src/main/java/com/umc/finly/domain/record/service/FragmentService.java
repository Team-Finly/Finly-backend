package com.umc.finly.domain.record.service;

import com.umc.finly.domain.record.dto.response.FragmentSummaryResDTO;
import org.springframework.data.repository.query.Param;

public interface FragmentService {
    FragmentSummaryResDTO getFragmentSummary(Long memberId);
}
