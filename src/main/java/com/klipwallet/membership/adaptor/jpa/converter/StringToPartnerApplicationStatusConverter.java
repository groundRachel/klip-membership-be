package com.klipwallet.membership.adaptor.jpa.converter;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.PartnerApplication;

@Component
public class StringToPartnerApplicationStatusConverter implements Converter<String, PartnerApplication.Status> {
    @Override
    public PartnerApplication.Status convert(String source) {
        return Optional.of(source).map(String::toUpperCase)
                       .map(PartnerApplication.Status::fromDisplay)
                       .orElse(null);
    }
}
