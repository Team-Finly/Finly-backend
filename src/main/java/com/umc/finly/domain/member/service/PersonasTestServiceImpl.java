package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.PersonaTestOptionResDTO;
import com.umc.finly.domain.member.dto.response.PersonaTestQuestionResDTO;
import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import com.umc.finly.domain.member.repository.PersonaTestOptionsRepository;
import com.umc.finly.domain.member.repository.PersonaTestQuestionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonasTestServiceImpl implements PersonaTestService {

    private final PersonaTestQuestionsRepository questionRepository;
    private final PersonaTestOptionsRepository optionRepository;

    // 페르소나 테스트 질문 전체 조회
    @Override
    public List<PersonaTestQuestionResDTO> getPersonasTestQuestions(){

        List<PersonaTestQuestion> questions =
                 questionRepository.findAllByOrderByQuestionCodeAsc();

        // 각 질문에 대한 선택지 조회 후 DTO 변환
        return questions.stream()
                .map(question->{
                    List<PersonaTestOptionResDTO> options =
                            optionRepository
                                    .findByQuestionIdOrderByChoiceCodeAsc(question.getId())
                                    .stream()
                                    .map(PersonaTestOptionResDTO::from)
                                    .toList();
                    return PersonaTestQuestionResDTO.of(question, options);
                })
                .toList();
    }
}
