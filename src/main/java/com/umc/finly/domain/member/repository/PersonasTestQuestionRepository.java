package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.PersonasTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonasTestQuestionRepository
        extends JpaRepository<PersonasTestQuestion, Long> {

    List<PersonasTestQuestion> findAllByOrderByQuestionCodeAsc();
}
