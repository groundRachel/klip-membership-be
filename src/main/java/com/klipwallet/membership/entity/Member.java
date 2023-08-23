package com.klipwallet.membership.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 이용자 Entity
 * <pre>
 * - 파트너사
 * - 관리자
 * </pre>
 */

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseEntity<Member> {
    @Column(unique = true)
    String email;
    @Column(unique = true)
    String oAuthId;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @JsonIgnore
    @Transient
    private MemberId memberId;

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
