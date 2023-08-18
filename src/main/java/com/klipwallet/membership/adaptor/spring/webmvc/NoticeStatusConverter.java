package com.klipwallet.membership.adaptor.spring.webmvc;

import jakarta.annotation.Nonnull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.entity.Notice.Status;

@Component
public class NoticeStatusConverter implements Converter<String, Notice.Status> {
    @Override
    public Status convert(@Nonnull String source) {
        return Notice.Status.fromDisplay(source);
    }
}
