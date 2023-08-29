package com.klipwallet.membership.adaptor.spring.webmvc;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.PartnerApplication;

@Component
public class StringToPartnerApplicationStatusConverter implements Converter<String, PartnerApplication.Status> {
    @Override
    public PartnerApplication.Status convert(@NonNull String source) {
        return Optional.of(source).map(String::toUpperCase)
                       .map(PartnerApplication.Status::fromDisplay)
                       .orElse(null);
    }
}
