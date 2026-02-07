package com.umc.finly.domain.member.entity;

import com.umc.finly.global.entity.CreatedDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_email", columnNames = "email")
        }
)
public class Member extends CreatedDeletedBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "persona_id")
    private Long personaId;

    @Column(nullable = false, name = "fin_mind_idx")
    private Integer finMindIdx;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "refresh_token_expired_at")
    private LocalDateTime refreshTokenExpiredAt;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    public void addProfileImage(String imageUrl){
        this.profileImageUrl = imageUrl;
    }

    public boolean hasProfileImage(){
        return this.profileImageUrl != null;
    }

    @PrePersist
    protected void onCreate(){
        if(finMindIdx == null){
            finMindIdx = 0;
        }
    }

    public void updateRefreshToken(String refreshToken, LocalDateTime expiredAt) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiredAt = expiredAt;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiredAt = null;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}

