package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.springframework.util.unit.DataSize;

@Converter(autoApply = true)
public class DataSizeConverter implements AttributeConverter<DataSize, Long> {
    @Override
    public Long convertToDatabaseColumn(DataSize attribute) {
        return attribute.toBytes();
    }

    @Override
    public DataSize convertToEntityAttribute(Long dbData) {
        return DataSize.ofBytes(dbData);
    }
}
