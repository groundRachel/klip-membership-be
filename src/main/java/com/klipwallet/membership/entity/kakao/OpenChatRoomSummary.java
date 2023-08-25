package com.klipwallet.membership.entity.kakao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

@Embeddable
@Value
public class OpenChatRoomSummary {
    @Column(name = "chatroom_id", nullable = false)
    Long id;
    @Column(name = "chatroom_url", nullable = false)
    String url;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected OpenChatRoomSummary() {
        this(null, null);
    }

    @JsonCreator
    public OpenChatRoomSummary(@JsonProperty("id") Long id, @JsonProperty("url") String url) {
        this.id = id;
        this.url = url;
    }
}
