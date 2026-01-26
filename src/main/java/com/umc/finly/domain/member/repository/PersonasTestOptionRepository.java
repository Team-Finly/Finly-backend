package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.PersonaTestOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonasTestOptionRepository
        extends JpaRepository<PersonaTestOption, Long> {

    List<PersonaTestOption> findByQuestionIdOrderByChoiceCodeAsc(Long questionId);
}
