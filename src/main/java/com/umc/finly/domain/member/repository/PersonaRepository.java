package com.umc.finly.domain.member.repository;

import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.domain.member.enums.PersonaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    // 페르소나 타입으로 페르소나 조회
    Optional<Persona> findByPersonaType(PersonaType personaType);
}
