package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * 채팅방 멤버 Entity
 * <p>
 * 채팅방 하나에 최대 1500 명이 참여할 수 있음
 * 채팅방 방장은 채팅방을 생성하면서 같이 만들어 준다. 최대 4명의 부방장을 만들 수 있다.
 * 고로 나머지 일반 멤버는 총 {@literal 1500 - 5 = 1495}명이 된다.
 * </p>
 */
@Entity
public class ChatRoomMember {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
}
