package com.umc.finly.domain.member.entity;

import com.umc.finly.domain.member.enums.ChoiceCode;
import com.umc.finly.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "personas_test_option")
public class PersonasTestOption extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private PersonasTestQuestion question;

    @Enumerated(EnumType.STRING)
    @Column(name = "choice_code", nullable = false)
    private ChoiceCode choiceCode;

    @Column(nullable = false, length = 300)
    private String content;

}
