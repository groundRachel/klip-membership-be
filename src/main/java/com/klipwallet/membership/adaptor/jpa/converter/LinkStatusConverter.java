package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.LinkStatus;

@Converter(autoApply = true)
public class LinkStatusConverter extends StatusableConverter<LinkStatus> {
    public LinkStatusConverter() {
        super(LinkStatus.class);
    }
}
