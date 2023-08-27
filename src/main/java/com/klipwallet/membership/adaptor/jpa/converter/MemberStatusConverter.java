package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.Member;

@Converter(autoApply = true)
public class MemberStatusConverter extends StatusableConverter<Member.Status> {
    public MemberStatusConverter() {
        super(Member.Status.class);
    }
}
