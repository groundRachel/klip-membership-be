package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * 채팅방 멤버 Entity
 * <p>
 * 채팅방 하나에 최대 1500 명이 참여할 수 있음
 * 채팅방 방장은 채팅방을 생성하면서 같이 만들어 준다. 최대 4명의 부방장을 만들 수 있다.
 * 고로 나머지 일반 멤버는 총 {@literal 1500 - 5 = 1495}명이 된다.
 * </p>
 */
@Entity
@DynamicUpdate
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Operator extends BaseEntity<Operator> {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;
    @Column(nullable = false)
    Long klipId;

    /**
     * 파트너 계정 Id
     * <p>
     * 파트너 계정 아래 여러 운영자들을 미리 등록하고
     * 이후 오픈채팅방 생성시에 방장 등록이나 운영진 등록에 사용한다.
     * {@link com.klipwallet.membership.entity.Partner#getId()}
     * <p>
     */
    @Column(nullable = false)
    Integer partnerId;

    @ForJpa
    protected Operator() {
    }

    public Operator(Long klipId, Integer partnerId, MemberId creatorId) {
        this.klipId = klipId;
        this.partnerId = partnerId;
        this.createBy(creatorId);
    }
}
