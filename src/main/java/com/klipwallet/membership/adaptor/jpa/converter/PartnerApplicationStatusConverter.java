package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.PartnerApplication;

@Converter(autoApply = true)
public class PartnerApplicationStatusConverter extends StatusableConverter<PartnerApplication.Status> {
    public PartnerApplicationStatusConverter() {
        super(PartnerApplication.Status.class);
    }
}
