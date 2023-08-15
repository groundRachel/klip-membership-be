package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * Entity 기본 속성 정보.
 * <p>
 *   <ul>
 *     <li>createdAt: 생성일시</li>
 *     <li>createdBy: 생성자 아이디</li>
 *     <li>updatedAt: 마지막 수정일시</li>
 *     <li>updatedBy: 마지막 수정자 아이디</li>
 *   </ul>
 * </p>
 */
@Embeddable
@Value
@AllArgsConstructor
public class BaseMeta {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    LocalDateTime createdAt;
    @Column(updatable = false, nullable = false)
    MemberId createdBy;
    @LastModifiedDate
    @Column(nullable = false)
    LocalDateTime updatedAt;
    @Column(nullable = false)
    MemberId updatedBy;

    @ForJpa
    public BaseMeta() {
        this.createdAt = null;
        this.createdBy = null;
        this.updatedAt = null;
        this.updatedBy = null;
    }

    public BaseMeta(MemberId createdBy) {
        this.createdAt = null;
        this.createdBy = createdBy;
        this.updatedAt = null;
        this.updatedBy = createdBy;
    }

    @ForJpa
    public BaseMeta withCreatedAt(LocalDateTime createdAt) {
        return new BaseMeta(createdAt, this.createdBy, this.updatedAt, this.updatedBy);
    }

    @ForJpa
    public BaseMeta withUpdatedAt(LocalDateTime updatedAt) {
        return new BaseMeta(this.createdAt, this.createdBy, updatedAt, this.updatedBy);
    }
}
