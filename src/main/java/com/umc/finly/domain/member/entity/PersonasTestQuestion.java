package com.umc.finly.domain.member.entity;

import com.umc.finly.domain.member.enums.QuestionCode;
import com.umc.finly.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter@Builder
@Table(name = "personas_test_question")
public class PersonasTestQuestion extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_code", nullable = false, unique = true)
    private QuestionCode questionCode;

    @Column(nullable = false, length = 500)
    private String content;

}
