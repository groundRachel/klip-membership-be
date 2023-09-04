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
public class ChatRoomNft extends BaseEntity<ChatRoomNft> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer partnerId;
    @Column(nullable = false)
    private Long chatRoomId;
    @Column(nullable = false)
    private Long dropId;
    @Column(nullable = false)
    private String contractAddress;

    @ForJpa
    protected ChatRoomNft() {
    }

    public ChatRoomNft(Integer partnerId, Long chatRoomId, Long dropId, Address contractAddress, MemberId creatorId) {
        this.partnerId = partnerId;
        this.chatRoomId = chatRoomId;
        this.dropId = dropId;
        this.contractAddress = contractAddress.getValue();
        this.createBy(creatorId);
    }
}
