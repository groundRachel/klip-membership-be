package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.Faq;

@Converter(autoApply = true)
public class FaqStatusConverter extends StatusableConverter<Faq.Status> {
    public FaqStatusConverter() {
        super(Faq.Status.class);
    }
}
