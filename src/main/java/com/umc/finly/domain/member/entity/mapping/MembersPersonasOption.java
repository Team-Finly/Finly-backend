package com.umc.finly.domain.member.entity.mapping;

import com.umc.finly.domain.member.entity.PersonasTestOption;
import com.umc.finly.domain.member.entity.PersonasTestQuestion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(
        name = "members_personas_option",
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
    private PersonasTestQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private PersonasTestOption option;
}
