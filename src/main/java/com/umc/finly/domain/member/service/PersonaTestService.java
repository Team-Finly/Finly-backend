package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.PersonaTestQuestionResDTO;

import java.util.List;

public interface PersonaTestService {
    List<PersonaTestQuestionResDTO> getPersonasTestQuestions();
}
