package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.OpenChatting;

@Converter(autoApply = true)
public class OpenChattingStatusConverter extends StatusableConverter<OpenChatting.Status> {
    public OpenChattingStatusConverter() {
        super(OpenChatting.Status.class);
    }
}
