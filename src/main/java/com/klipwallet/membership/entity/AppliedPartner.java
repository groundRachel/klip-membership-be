package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppliedPartner extends Member {

    private String name;
    private String phoneNumber;
    private String businessRegistrationNumber;

    public AppliedPartner(String name, String phoneNumber, String businessRegistrationNumber, String email, @NotBlank String oAuthId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.email = email;
        this.oAuthId = oAuthId;
    }

    public AppliedPartner() {
    }
}
