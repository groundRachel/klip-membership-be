package com.klipwallet.membership.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class OpenChattingNft extends BaseEntity<OpenChattingNft> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long openChattingId;
    @Column(unique = true)
    private Long dropId;
    @Column(nullable = false)
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "sca"))
    private Address sca;

    @ForJpa
    protected OpenChattingNft() {
    }

    public OpenChattingNft(Long openChattingId, Long dropId, Address sca, MemberId creatorId) {
        this.openChattingId = openChattingId;
        this.dropId = dropId;
        this.sca = sca;
        this.createBy(creatorId);
    }
}
