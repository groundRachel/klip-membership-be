package com.klipwallet.membership.entity;

import jakarta.annotation.Nullable;

import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;

public interface Attachable extends InputStreamSource {
    @Nullable
    String getFilename();

    MediaType getContentType();

    long getBytesSize();
}
