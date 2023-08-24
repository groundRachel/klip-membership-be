package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.entity.ChatRoom.Source;

import static com.klipwallet.membership.entity.Statusable.requireVerifiedCode;

/**
 * 채팅방 멤버 Entity
 * <p>
 * 채팅방 하나에 최대 1500 명이 참여할 수 있음
 * 채팅방 방장은 채팅방을 생성하면서 같이 만들어 준다. 최대 4명의 부방장을 만들 수 있다.
 * 고로 나머지 일반 멤버는 총 {@literal 1500 - 5 = 1495}명이 된다.
 * </p>
 */
@Entity
@Value
@RequiredArgsConstructor
public class ChatRoomMember {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;
    @Column(name = "nickname")
    String nickname;
    @Column(name = "profile_image")
    String profileImage;
    @Column(name = "role")
    Role role;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    ChatRoom chatRoom;

    public ChatRoomMember() {
        id = null;
        nickname = null;
        profileImage = null;
        chatRoom = null;
        role = null;
    }

    @Getter
    @Schema(name = "ChatRoomMember.Role", description = "채팅방 멤버 역할", example = "host")
    public enum Role implements Statusable {
        HOST(0),
        SUB_HOST(1),
        MEMBER(2);
        private final byte code;

        Role(int code) {
            this.code = requireVerifiedCode(code);
        }

        @JsonCreator
        @Nullable
        public static Source fromDisplay(String display) {
            return Statusable.fromDisplay(Source.class, display);
        }

        @JsonValue
        @Override
        public String toDisplay() {
            return Statusable.super.toDisplay();
        }
    }
}
