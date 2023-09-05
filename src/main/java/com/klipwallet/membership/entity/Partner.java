package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@DiscriminatorValue("P")
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Partner extends Member {
    @Column(nullable = false)
    private Integer partnerApplicationId; // todo foreign key
    private Integer klipDropsPartnerId;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String businessRegistrationNumber;

    public Partner(Integer partnerApplicationId, Integer klipDropsPartnerId, String name, String phoneNumber, String businessRegistrationNumber,
                   String email, String oAuthId, MemberId creator) {
        this.partnerApplicationId = partnerApplicationId;
        this.klipDropsPartnerId = klipDropsPartnerId;
        setName(name);
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        setEmail(email);
        setOauthId(oAuthId);
        setStatus(Status.ACTIVATED);
        createBy(creator);
    }

    public Partner() {
    }

    public void update(String name, String phoneNumber) {
        this.setName(name);
        this.phoneNumber = phoneNumber;
    }
}
