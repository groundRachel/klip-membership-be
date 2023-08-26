package com.klipwallet.membership.adaptor.spring.webmvc;

import jakarta.annotation.Nonnull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.entity.Notice.Status;

@Component
public class NoticeStatusConverter implements Converter<String, Notice.Status> {
    @Override
    public Status convert(@Nonnull String source) {
        Status result = Status.fromDisplay(source);
        if (result == null) {
            return null;
        }
        // 유효하지 않는 상태(ex: delete)로의 변환은 불가능함. delete는 내부에서만 사용 가능한 논리적 삭제 상태임
        if (!result.isEnabled()) {
            throw new IllegalArgumentException("Invalid notice status: %s".formatted(source));
        }
        return result;
    }
}
