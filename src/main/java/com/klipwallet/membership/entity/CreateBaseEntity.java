package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 자세한 내용은 {@link com.klipwallet.membership.entity.BaseEntity} 참고
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class CreateBaseEntity<E extends CreateBaseEntity<E>> extends AbstractAggregateRoot<E> {
    /**
     * 생성일시
     *
     * @see org.springframework.data.annotation.CreatedDate
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false, nullable = false)
    private MemberId creatorId;

    /**
     * 생성자 설정
     *
     * @param creatorId 생성자 아이디
     */
    protected void createBy(@NonNull MemberId creatorId) {
        this.creatorId = creatorId;
    }
}
