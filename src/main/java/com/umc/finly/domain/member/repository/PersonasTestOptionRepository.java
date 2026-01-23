package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.PersonasTestOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonasTestOptionRepository
        extends JpaRepository<PersonasTestOption, Long> {

    List<PersonasTestOption> findByQuestionIdOrderByChoiceCodeAsc(Long questionId);
}
