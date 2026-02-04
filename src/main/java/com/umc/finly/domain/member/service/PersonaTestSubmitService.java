package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReqDTO;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitResDTO;

public interface PersonaTestSubmitService {

    PersonaTestSubmitResDTO submit(String mode, Long memberId, PersonaTestSubmitReqDTO request);
}
