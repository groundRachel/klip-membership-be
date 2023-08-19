package com.klipwallet.membership.entity;

import java.time.LocalDateTime;
import java.util.Set;

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
 *
 * @deprecated 이제 모든 Entity는 기본 모델을 합성 기반 {@link com.klipwallet.membership.entity.BaseMeta} 에서
 * 상속 기반 {@link com.klipwallet.membership.entity.BaseEntity} 로 변경한다.
 */
@Deprecated(since = "0.2.0")
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

    public BaseMeta(MemberId creator) {
        this.createdAt = null;
        this.createdBy = creator;
        this.updatedAt = null;
        this.updatedBy = creator;
    }

    @ForJpa
    public BaseMeta withCreatedAt(LocalDateTime createdAt) {
        return new BaseMeta(createdAt, this.createdBy, this.updatedAt, this.updatedBy);
    }

    @ForJpa
    public BaseMeta withUpdatedAt(LocalDateTime updatedAt) {
        return new BaseMeta(this.createdAt, this.createdBy, updatedAt, this.updatedBy);
    }

    public BaseMeta withUpdatedBy(MemberId updater) {
        return new BaseMeta(this.createdAt, this.createdBy, this.updatedAt, updater);
    }

    /**
     * 접근자들 조회
     * <p>
     * 생성자 + 마지막 수정자
     * </p>
     */
    public Set<MemberId> getAccessorIds() {
        if (equalCreatorAndUpdater()) {
            return Set.of(this.getCreatedBy());
        }
        return Set.of(this.getCreatedBy(), this.getUpdatedBy());
    }

    private boolean equalCreatorAndUpdater() {
        return this.getCreatedBy().equals(this.getUpdatedBy());
    }
}
