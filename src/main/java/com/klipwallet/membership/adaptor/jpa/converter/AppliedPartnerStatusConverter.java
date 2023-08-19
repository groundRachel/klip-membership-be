package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.AppliedPartner;

@Converter(autoApply = true)
public class AppliedPartnerStatusConverter extends StatusableConverter<AppliedPartner.Status> {
    public AppliedPartnerStatusConverter() {
        super(AppliedPartner.Status.class);
    }
}
