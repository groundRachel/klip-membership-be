package com.klipwallet.membership.entity;

import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * 블록 주소 ValueObject
 * <p>
 * 0xa005e82487fb629923b9598fffd1c2e9499f0cab
 * </p>
 */
@Embeddable
@Value
public class Address {
    String value;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected Address() {
        this(null);
    }

    @JsonCreator
    public Address(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
