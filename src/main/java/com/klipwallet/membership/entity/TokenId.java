package com.klipwallet.membership.entity;

import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.exception.InvalidRequestException;

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

    /**
     * klip dropId 얻기
     * value 35100240011
     * return 3510024
     */
    public Long getKlipDropId() {
        if (this.value.length() < 4) {
            throw new InvalidRequestException();
        }
        String dropId = this.value.substring(0, this.value.length() - 4);
        return Long.parseLong(dropId);
    }
}
