package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

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

    @Column(unique = true)
    String email;
    @Column(unique = true)
    String oAuthId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @LastModifiedBy
    private Integer updatedBy; // TODO KLDV-3069 check if the value is changed

    public Member() {
        this.id = null;
    }
}
