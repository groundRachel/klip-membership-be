package com.klipwallet.membership.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;

public record MemberId(@JsonValue @NonNull Integer value) implements Serializable {
    @JsonCreator
    public MemberId {
    }
}
