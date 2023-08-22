package com.klipwallet.membership.adaptor.spring.webmvc;

import jakarta.annotation.Nonnull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.Faq.Status;

@Component
public class FaqStatusConverter implements Converter<String, Status> {
    @Override
    public Status convert(@Nonnull String source) {
        return Status.fromDisplay(source);
    }
}
