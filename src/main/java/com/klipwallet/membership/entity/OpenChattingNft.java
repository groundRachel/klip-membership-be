package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"openChattingId", "dropId"})
})
public class OpenChattingNft extends BaseEntity<OpenChattingNft> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long openChattingId;
    @Column(nullable = false)
    private Long dropId;
    @Column(nullable = false)
    private String klipDropsSca;

    @ForJpa
    protected OpenChattingNft() {
    }

    public OpenChattingNft(Long openChattingId, Long dropId, Address klipDropsSca, MemberId creatorId) {
        this.openChattingId = openChattingId;
        this.dropId = dropId;
        this.klipDropsSca = klipDropsSca.getValue();
        this.createBy(creatorId);
    }
}
