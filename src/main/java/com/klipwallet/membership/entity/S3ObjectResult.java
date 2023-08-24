package com.klipwallet.membership.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

@Embeddable
@Value
public class S3ObjectResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -4419724155394908795L;

    @JsonValue
    String key;

    @JsonValue
    String url;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected S3ObjectResult() {
        this.key = null;
        this.url = null;
    }

    @JsonCreator
    public S3ObjectResult(@NonNull String key, @NonNull String url) {
        this.key = key;
        this.url = url;
    }
}
