package com.umc.finly.domain.auth.entity;

import com.umc.finly.domain.auth.enums.TermType;
import com.umc.finly.global.entity.CreatedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "terms")
public class Term extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false, length = 50, unique = true)
    private TermType termType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
