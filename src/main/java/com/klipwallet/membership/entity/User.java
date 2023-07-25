package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * 이용자 Entity
 * <pre>
 * - 파트너사
 * - 관리자
 * </pre>
 */
@Entity
public class User {
    @Id
    private UserId id;
}
