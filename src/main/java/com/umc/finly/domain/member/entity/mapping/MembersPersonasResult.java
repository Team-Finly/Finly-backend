package com.umc.finly.domain.member.entity.mapping;

import com.umc.finly.domain.member.entity.Personas;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(
        name = "members_personas_result",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_persona",
                        columnNames = {"mem_id"}
                )
        }
)
public class MembersPersonasResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mem_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Personas persona;
}
