package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Partner extends Member {
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String businessRegistrationNumber;

    public Partner(String name, String phoneNumber, String businessRegistrationNumber, String email, String oAuthId, MemberId creator) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.email = email;
        this.oAuthId = oAuthId;
        createBy(creator);
    }

    public Partner() {

    }
}
