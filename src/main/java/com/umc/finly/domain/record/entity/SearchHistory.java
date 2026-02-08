package com.umc.finly.domain.record.entity;

import com.umc.finly.global.entity.CreatedUpdatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

// 검색 기록 엔티티
// 사용자의 최근 검색 키워드를 저장함 (최대 3개까지 표시)
@Entity
@SQLRestriction("deleted_at IS NULL") // Soft Delete 조건 (삭제된 레코드 제외)
@Table(name = "search_history",
        uniqueConstraints = {
                // 회원 + 키워드 조합 유니크 (중복 키워드 방지)
                @UniqueConstraint(
                        name = "uk_search_history_member_keyword",
                        columnNames = {"member_id", "keyword"}
                )
        },
        indexes = {
                // 최근 검색어 조회 성능을 위한 인덱스
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

    // 검색한 회원 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 검색 키워드 (최대 100자)
    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;
}
