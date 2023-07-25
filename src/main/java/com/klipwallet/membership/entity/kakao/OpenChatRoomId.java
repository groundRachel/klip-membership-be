package com.klipwallet.membership.entity.kakao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;
import lombok.Value;

@Embeddable
@Value
@ToString
public class OpenChatRoomId {
    @JsonValue
    @Column(name = "openChatRoomId", unique = true, nullable = false)
    String id;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    protected OpenChatRoomId() {
        this(null);
    }

    @JsonCreator
    public OpenChatRoomId(String id) {
        this.id = id;
    }
}
