package com.klipwallet.membership.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
public class Partner extends Member {

    private String name;
    private String phoneNumber;
    private String businessRegistrationNumber;

    public Partner(String name, String phoneNumber, String businessRegistrationNumber, String email, @NotBlank String oAuthID) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.email = email;
        this.oAuthID = oAuthID;
    }


    protected Partner() {
    }
}
