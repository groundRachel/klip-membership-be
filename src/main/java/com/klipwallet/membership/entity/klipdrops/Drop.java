package com.klipwallet.membership.entity.klipdrops;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@RequiredArgsConstructor
public class Drop {
    @Column(name = "drop_id")
    Long id;

    public Drop() {
        id = null;
    }
}
