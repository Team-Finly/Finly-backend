package com.umc.finly.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedDeletedBaseEntity extends CreatedBaseEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // soft delete
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
