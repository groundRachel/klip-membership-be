package com.klipwallet.membership.entity.kakao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

@Embeddable
@Value
public class OpenChatRoomSummary {
    @JsonValue
    @Column(name = "chatroom_id", nullable = false)
    Long id;
    @JsonValue
    @Column(name = "chatroom_url", nullable = false)
    String url;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected OpenChatRoomSummary() {
        this(null, null);
    }

    @JsonCreator
    public OpenChatRoomSummary(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}
