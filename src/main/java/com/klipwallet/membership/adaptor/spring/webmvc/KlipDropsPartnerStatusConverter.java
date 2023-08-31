package com.klipwallet.membership.adaptor.spring.webmvc;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartnerStatus;

@Component
public class KlipDropsPartnerStatusConverter implements Converter<String, KlipDropsPartnerStatus> {
    @Override
    public KlipDropsPartnerStatus convert(@NonNull String source) {
        return Optional.of(source).map(String::toUpperCase)
                       .map(KlipDropsPartnerStatus::fromDisplay)
                       .orElse(null);
    }
}

