package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.PersonaTestOptionRes;
import com.umc.finly.domain.member.dto.response.PersonaTestQuestionRes;
import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import com.umc.finly.domain.member.repository.PersonasTestOptionRepository;
import com.umc.finly.domain.member.repository.PersonasTestQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonaTestServiceImpl implements PersonaTestService {

    private final PersonasTestQuestionRepository questionRepository;
    private final PersonasTestOptionRepository optionRepository;

    @Override
    public List<PersonaTestQuestionRes> getPersonasTestQuestions(){

        List<PersonaTestQuestion> questions =
                 questionRepository.findAllByOrderByQuestionCodeAsc();

        return questions.stream()
                .map(question->{
                    List<PersonaTestOptionRes> options =
                            optionRepository
                                    .findByQuestionIdOrderByChoiceCodeAsc(question.getId())
                                    .stream()
                                    .map(PersonaTestOptionRes::from)
                                    .toList();
                    return PersonaTestQuestionRes.of(question, options);
                })
                .toList();
    }
}
