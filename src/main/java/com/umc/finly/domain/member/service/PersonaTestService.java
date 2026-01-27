package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.PersonaTestQuestionRes;

import java.util.List;

public interface PersonaTestService {
    List<PersonaTestQuestionRes> getPersonasTestQuestions();
}
