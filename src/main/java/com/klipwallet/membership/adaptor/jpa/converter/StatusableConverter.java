package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.AttributeConverter;

import com.klipwallet.membership.entity.Statusable;

abstract class StatusableConverter<T extends Enum<T> & Statusable> implements AttributeConverter<T, Byte> {
    private final Class<T> sClass;

    StatusableConverter(Class<T> sClass) {
        this.sClass = sClass;
    }

    @Override
    public final Byte convertToDatabaseColumn(T attribute) {
        return attribute.getCode();
    }

    @Override
    public final T convertToEntityAttribute(Byte dbData) {
        return Statusable.fromCode(sClass, dbData);
    }
}
