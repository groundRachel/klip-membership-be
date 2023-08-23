package com.klipwallet.membership.entity;

import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;

public interface Attachable extends InputStreamSource {
    String getFilename();

    MediaType getContentType();

    long getBytesSize();
}
