package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.Member;

@Converter(autoApply = true)
public class MemberTypeConverter implements AttributeConverter<Member.Type, String> {
    @Override
    public String convertToDatabaseColumn(Member.Type attribute) {
        return attribute.getCode();
    }

    @Override
    public Member.Type convertToEntityAttribute(String dbData) {
        return Member.Type.fromCode(dbData);
    }
}
