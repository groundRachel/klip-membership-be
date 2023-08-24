package com.klipwallet.membership.entity;

import javax.annotation.Nullable;

import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;

public interface Attachable extends InputStreamSource {
    @Nullable
    String getFileName();

    @Nullable
    MediaType getContentType();

    long getBytesSize();
}
