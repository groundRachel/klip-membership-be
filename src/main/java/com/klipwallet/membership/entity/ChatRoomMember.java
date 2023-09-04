package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.entity.ChatRoom.Source;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class ChatRoomMember {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;
    @Column(nullable = false)
    private Long klipId;
    @Column(nullable = false)
    private String kakaoUserId;
    @Column
    private Long operatorId;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String profileImageUrl;
    @Column(nullable = false)
    private Role role;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ForJpa
    protected ChatRoomMember() {
    }

    public ChatRoomMember(ChatRoom chatRoom, Long klipId, String kakaoUserId, Long operatorId, String nickname, String profileImageUrl,
                          Role role) {
        this.chatRoom = chatRoom;
        this.klipId = klipId;
        this.kakaoUserId = kakaoUserId;
        this.operatorId = operatorId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    @Getter
    @Schema(name = "ChatRoomMember.Role", description = "채팅방 멤버 역할", example = "host")
    public enum Role implements Statusable {
        HOST(0),
        OPERATOR(1),
        NFT_HOLDER(2);
        private final byte code;

        Role(int code) {
            this.code = Statusable.requireVerifiedCode(code);
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

