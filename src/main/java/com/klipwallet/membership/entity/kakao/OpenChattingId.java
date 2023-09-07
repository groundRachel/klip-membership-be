package com.klipwallet.membership.entity.kakao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

@Embeddable
@Value
@ToString
public class OpenChattingId {
    @JsonValue
    @Column(name = "openChattingId", nullable = false)
    String id;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected OpenChattingId() {
        this(null);
    }

    @JsonCreator
    public OpenChattingId(String id) {
        this.id = id;
    }
}
