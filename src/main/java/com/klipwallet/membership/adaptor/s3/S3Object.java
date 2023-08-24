package com.klipwallet.membership.adaptor.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.klipwallet.membership.entity.Attachable;

public record S3Object(MultipartFile file) implements Attachable {

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public MediaType getContentType() {
        if (Objects.equals(file.getContentType(), null)) {
            return null;
        } else {
            return MediaType.valueOf(file.getContentType());
        }
    }

    @Override
    public long getBytesSize() {
        return file.getSize();
    }

    @Override
    @NonNull
    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }
}
