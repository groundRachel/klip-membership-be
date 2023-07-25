package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

public abstract class DomainEvent {
    private final LocalDateTime occurredOn = LocalDateTime.now();

    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}
