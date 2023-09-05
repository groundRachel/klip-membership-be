package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@DiscriminatorValue("P")
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Partner extends Member {

    @OneToOne
    @JoinColumn(name = "partner_application_id", nullable = false)
    private PartnerApplication partnerApplication;
    private Integer klipDropsPartnerId;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String businessRegistrationNumber;

    public Partner(PartnerApplication partnerApplication, Integer klipDropsPartnerId, String name, String phoneNumber,
                   String businessRegistrationNumber,
                   String email, String oAuthId, MemberId creator) {
        this.partnerApplication = partnerApplication;
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
