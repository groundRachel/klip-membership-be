package com.klipwallet.membership.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

/**
 * 이용자 아이디 ValueObject
 */
@Embeddable
@Value
public class UserId implements Serializable {
    @Column(name = "id")
    Integer value;

    public UserId() {
        this(null);
    }

    @JsonCreator
    public UserId(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }
}
