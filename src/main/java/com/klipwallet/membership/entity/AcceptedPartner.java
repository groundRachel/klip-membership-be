package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.klipwallet.membership.entity.AppliedPartner.Status;

@Entity
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AcceptedPartner extends Member {

    private String name;
    private String phoneNumber;
    private String businessRegistrationNumber;

    public AcceptedPartner(String name, String phoneNumber, String businessRegistrationNumber, String email, String oAuthId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.email = email;
        this.oAuthId = oAuthId;
    }

    public AcceptedPartner() {
        
    }
}
