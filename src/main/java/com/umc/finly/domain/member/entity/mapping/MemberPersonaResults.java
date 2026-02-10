package com.umc.finly.domain.member.entity.mapping;

import com.umc.finly.domain.member.entity.Persona;
import com.umc.finly.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

// 멤버-페르소나 테스트 결과
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(
        name = "member_persona_results",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_persona",
                        columnNames = {"member_id"}
                )
        }
)
public class MemberPersonaResults extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    // 기존 결과 갱신
    public MemberPersonaResults updatePersona(Persona newPersona){
        this.persona = newPersona;
        return this;
    }

    // 최초 생성
    public static MemberPersonaResults create(Long memberId, Persona persona){
        return MemberPersonaResults.builder()
                .memberId(memberId)
                .persona(persona)
                .build();
    }
}
