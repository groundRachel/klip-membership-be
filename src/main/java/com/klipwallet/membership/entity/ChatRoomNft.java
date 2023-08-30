package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
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
public class ChatRoomNft {
    @Column(nullable = false)
    Integer partnerId;
    @Column(nullable = false)
    Long chatRoomId;
    @Column(nullable = false)
    Long dropId;
    @Column(nullable = false)
    Address sca;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ForJpa
    protected ChatRoomNft() {
    }

    public ChatRoomNft(Integer partnerId, Long chatRoomId, Long dropId, Address sca) {
        this.partnerId = partnerId;
        this.chatRoomId = chatRoomId;
        this.dropId = dropId;
        this.sca = sca;
    }
}
