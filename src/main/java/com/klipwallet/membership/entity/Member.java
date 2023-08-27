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
 *
 * @see com.klipwallet.membership.entity.Partner
 * @see com.klipwallet.membership.entity.Admin
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Member extends BaseEntity<Member> {
    @Column(unique = true)
    String email;
    /**
     * oAuthId
     * <p>
     * 구글 oauth2 id(sub)
     * </p>
     */
    @Column(unique = true)
    String oAuthId;
    /**
     * 표시 이름
     * <p>
     * - partner: 회사명<br/>
     * - admin: 이메일 LocalPart<br/>
     * </p>
     */
    @Column(nullable = false)
    String name;
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
