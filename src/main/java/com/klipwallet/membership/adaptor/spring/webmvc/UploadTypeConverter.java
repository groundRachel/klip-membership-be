package com.klipwallet.membership.adaptor.spring.webmvc;

import jakarta.annotation.Nonnull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.UploadType;

@Component
public class UploadTypeConverter implements Converter<String, UploadType> {
    @Override
    public UploadType convert(@Nonnull String source) {
        UploadType result = UploadType.fromDisplay(source);
        if (result == null) {
            return null;
        }
        // 유효하지 않는 상태(ex: editor)로의 변환은 불가능함. "editor"은 admin 전용 api에서만 사용 가능
        if (result == UploadType.EDITOR) {
            throw new IllegalArgumentException("Invalid upload type: %s".formatted(source));
        }
        return result;
    }
}
