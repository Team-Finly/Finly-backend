package com.umc.finly.domain.auth.entity.mapping;

import com.umc.finly.domain.auth.entity.Term;
import com.umc.finly.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "members_terms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_term", columnNames = {"member_id", "term_id"})
        }
)
public class MemberTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(name = "is_agreed", nullable = false)
    private Boolean isAgreed;

}
