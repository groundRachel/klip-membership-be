package com.klipwallet.membership.entity;

import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;

public interface Attachable extends InputStreamSource {
    String getFileName();

    MediaType getContentType();

    long getBytesSize();
}
