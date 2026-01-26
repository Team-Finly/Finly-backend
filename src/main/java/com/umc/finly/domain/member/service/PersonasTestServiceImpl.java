package com.umc.finly.domain.member.service;

import com.umc.finly.domain.member.dto.response.PersonasTestOptionRes;
import com.umc.finly.domain.member.dto.response.PersonasTestQuestionRes;
import com.umc.finly.domain.member.entity.PersonasTestQuestion;
import com.umc.finly.domain.member.repository.PersonasTestOptionRepository;
import com.umc.finly.domain.member.repository.PersonasTestQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonasTestServiceImpl implements PersonasTestService{

    private final PersonasTestQuestionRepository questionRepository;
    private final PersonasTestOptionRepository optionRepository;

    // 페르소나 테스트 질문 전체 조회
    @Override
    public List<PersonasTestQuestionRes> getPersonasTestQuestions(){

        List<PersonasTestQuestion> questions =
                 questionRepository.findAllByOrderByQuestionCodeAsc();

        // 각 질문에 대한 선택지 조회 후 DTO 변환
        return questions.stream()
                .map(question->{
                    List<PersonasTestOptionRes> options =
                            optionRepository
                                    .findByQuestionIdOrderByChoiceCodeAsc(question.getId())
                                    .stream()
                                    .map(PersonasTestOptionRes::from)
                                    .toList();
                    return PersonasTestQuestionRes.of(question, options);
                })
                .toList();
    }
}
