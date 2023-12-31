package com.klipwallet.membership.dto.faq;

import jakarta.annotation.Nonnull;

import com.klipwallet.membership.entity.MemberId;


public record FaqUpdatable(String title,
                           String body,
                           MemberId updaterId) implements com.klipwallet.membership.entity.FaqUpdatable {
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

    @Nonnull
    @Override
    public MemberId getUpdaterId() {
        return this.updaterId();
    }
}
