package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.PersonasTestQuestionRes;

import java.util.List;

public interface PersonasTestService {
    List<PersonasTestQuestionRes> getPersonasTestQuestions();
}
