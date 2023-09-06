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
import com.klipwallet.membership.exception.kakao.OperatorNotInPartnerException;

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
    /**
     * 디비에 저장된 클립 계정 Id
     * {@link com.klipwallet.membership.adaptor.klip.KlipAccount#getKlipAccountId()}
     */
    @Column(nullable = false)
    Long klipId;
    @Column(nullable = false)
    String kakaoUserId;
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

    /**
     * 운영진 생성
     *
     * @param klipId 운영진의 Klip 이용자 ID
     * @param kakaoUserId 운영진의 카카오 이용자ID
     * @param partnerId 가입
     */
    public Operator(Long klipId, String kakaoUserId, MemberId partnerId) {
        this.klipId = klipId;
        this.kakaoUserId = kakaoUserId;
        this.partnerId = partnerId.value();
        this.createBy(partnerId);
    }

    public void checkPartnerId(MemberId partnerId) {
        if (!this.getPartnerId().equals(partnerId.value())) {
            throw new OperatorNotInPartnerException(this.getId(), partnerId);
        }
    }
}
