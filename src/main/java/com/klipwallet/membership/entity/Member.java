package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 이용자 Entity
 * <pre>
 * - 파트너사
 * - 관리자
 * </pre>
 */
@Entity
@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Member {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    public Member() {
        this.id = null;
    }
}
