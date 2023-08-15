package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 이용자 Entity
 * <pre>
 * - 파트너사
 * - 관리자
 * </pre>
 */

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @JsonIgnore
    @Transient
    private MemberId memberId;

    @Column(unique = true)
    String email;
    @Column(unique = true)
    String oAuthId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @CreatedBy
    @Column(updatable = false)
    private MemberId createdBy;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @LastModifiedBy
    private MemberId updatedBy; // TODO KLDV-3069 check if the value is changed

    @Nullable
    @JsonIgnore
    public MemberId getMemberId() {
        if (id == null) {
            return null;
        }
        if (memberId == null) {
            this.memberId = new MemberId(id);
        }
        return this.memberId;
    }
}
