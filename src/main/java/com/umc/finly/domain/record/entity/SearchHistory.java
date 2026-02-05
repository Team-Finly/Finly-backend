package com.umc.finly.domain.record.entity;

import com.umc.finly.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "search_history",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_search_history_member_keyword",
                        columnNames = {"member_id", "keyword"}
                )
        },
        indexes = {
                @Index(name = "idx_search_history_member_updated", columnList = "member_id, updated_at DESC")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SearchHistory extends CreatedUpdatedDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;
}
