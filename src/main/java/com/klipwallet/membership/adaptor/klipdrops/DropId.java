package com.klipwallet.membership.adaptor.klipdrops;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@RequiredArgsConstructor
public class DropId {
    @Column(name = "drop_id")
    Long id;

    public DropId() {
        id = null;
    }
}