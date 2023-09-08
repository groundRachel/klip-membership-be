package com.klipwallet.membership.adaptor.spring.webmvc;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.OpenChatting.Status;

@Component
public class OpenChattingStatusConverter implements Converter<String, Status> {
    @Override
    public Status convert(@NonNull String source) {
        return Status.fromDisplay(source);
    }
}
