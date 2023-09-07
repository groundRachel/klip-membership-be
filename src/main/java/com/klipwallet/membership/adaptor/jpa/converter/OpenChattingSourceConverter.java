package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.OpenChatting;

@Converter(autoApply = true)
public class OpenChattingSourceConverter extends StatusableConverter<OpenChatting.Source> {
    public OpenChattingSourceConverter() {
        super(OpenChatting.Source.class);
    }
}
