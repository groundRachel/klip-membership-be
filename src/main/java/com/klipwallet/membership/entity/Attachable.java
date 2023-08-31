package com.klipwallet.membership.entity;

import javax.annotation.Nullable;

import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.util.unit.DataSize;

public interface Attachable extends InputStreamSource {
    @Nullable
    String getFileName();

    MediaType getContentType();

    DataSize getSize();
}
