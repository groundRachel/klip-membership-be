package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

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
    private String phoneNumber;

    @Column(nullable = false)
    private String businessRegistrationNumber;

    public Partner(String name, String phoneNumber, String businessRegistrationNumber, String email, String oAuthId, MemberId creator) {
        setName(name);
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        setEmail(email);
        setOAuthId(oAuthId);
        setStatus(Status.ACTIVATED);
        createBy(creator);
    }

    public Partner() {
    }

    public interface PartnerSummary {
        MemberId getMemberId();

        String getName();

        LocalDateTime getProcessedAt();

        MemberId getProcessorId();

        // TODO 오픈채팅 운영 여부 정보 포함하기
    }
}
