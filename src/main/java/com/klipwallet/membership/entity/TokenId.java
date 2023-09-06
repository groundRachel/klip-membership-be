package com.klipwallet.membership.entity;

import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * 토큰 id ValueObject
 * <p>
 * 35100240011
 * </p>
 */
@Embeddable
@Value
public class TokenId {
    String value;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected TokenId() {
        this(null);
    }

    @JsonCreator
    public TokenId(String value) {
        this.value = value;
    }

    public String getHexString() {
        long decimalValue = Long.parseLong(this.value);
        return  "0x" + Long.toHexString(decimalValue);
    }
}
