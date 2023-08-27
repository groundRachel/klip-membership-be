package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * 어드민 Entity
 */
@Entity
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Admin extends Member {
    @ForJpa
    protected Admin() {
    }

    public Admin(String email, MemberId creator) {
        this.email = email;
        this.name = toLocalPart(email);
        createBy(creator);
    }

    private String toLocalPart(String email) {
        return StringUtils.substringBefore(email, "@");
    }
}
