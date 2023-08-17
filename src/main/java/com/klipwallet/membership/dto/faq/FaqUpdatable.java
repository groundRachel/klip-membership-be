package com.klipwallet.membership.dto.faq;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.klipwallet.membership.entity.Faq.Status;
import com.klipwallet.membership.entity.MemberId;


public record FaqUpdatable(String title,
                           String body,
                           Status status,
                           MemberId updatedBy) implements com.klipwallet.membership.entity.FaqUpdatable {
    @Nonnull
    @Override
    public String getTitle() {
        return this.title();
    }

    @Nonnull
    @Override
    public String getBody() {
        return this.body();
    }

    @Nullable
    @Override
    public Status getStatus() {
        return this.status();
    }

    @Nonnull
    @Override
    public MemberId getUpdatedBy() {
        return this.updatedBy();
    }
}
