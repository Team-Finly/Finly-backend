package com.umc.finly.domain.member.entity.mapping;

import com.umc.finly.domain.member.entity.PersonaTestOption;
import com.umc.finly.domain.member.entity.PersonaTestQuestion;
import jakarta.persistence.*;
import lombok.*;

// 멤버-페르소나 테스트 질문에 대한 선택지
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(
        name = "member_persona_options",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_question",
                        columnNames = {"mem_id", "question_id"}
                )
        }
)
public class MembersPersonasOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mem_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private PersonaTestQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private PersonaTestOption option;
}
