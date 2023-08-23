package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.springframework.http.MediaType;

@Converter(autoApply = true)
public class MediaTypeConverter implements AttributeConverter<MediaType, String> {

    @Override
    public String convertToDatabaseColumn(MediaType attribute) {
        return attribute.toString();
    }

    @Override
    public MediaType convertToEntityAttribute(String dbData) {
        return MediaType.valueOf(dbData);
    }
}
