package com.klipwallet.membership.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * 저장된 파일의 객체 아이디
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Embeddable
@Value
public class ObjectId implements Serializable {
    @Serial
    private static final long serialVersionUID = -4419724155394908795L;

    @JsonValue
    @Column(name = "object_id", nullable = false)
    String value;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected ObjectId() {
        this.value = null;
    }

    @JsonCreator
    public ObjectId(@NonNull String value) {
        this.value = value;
    }
}
