package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.request.PersonaTestSubmitReq;
import com.umc.finly.domain.member.dto.response.PersonaTestSubmitRes;

public interface PersonaTestSubmitService {

    PersonaTestSubmitRes submit(String mode, PersonaTestSubmitReq request);
}
