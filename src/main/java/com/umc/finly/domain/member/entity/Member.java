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

    /** 마이페이지 - 프로필 사진 관련 메서드 **/
    // 이미지 추가
    public void addProfileImage(String imageUrl){
        this.profileImageUrl = imageUrl;
    }

    // 이미지 삭제
    public void clearProfileImage() {
        this.profileImageUrl = null;
    }

    // TODO: 코드 리팩토링 예정
    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    // 프로필 이미지 존재 유무
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
    public void changePassword(String encodedPassword) { this.password = encodedPassword; }
}

