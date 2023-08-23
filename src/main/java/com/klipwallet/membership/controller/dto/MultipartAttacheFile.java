package com.klipwallet.membership.controller.dto;

import java.io.IOException;
import java.io.InputStream;

import jakarta.annotation.Nonnull;

import lombok.NonNull;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.klipwallet.membership.entity.Attachable;

@SuppressWarnings("ClassCanBeRecord")
@Value
public class MultipartAttacheFile implements Attachable {
    @SuppressWarnings("NullableProblems")
    @NonNull
    MultipartFile file;

    @Override
    public String getFilename() {
        return file.getOriginalFilename();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public MediaType getContentType() {
        return MediaType.parseMediaType(file.getContentType());
    }

    @Override
    public long getBytesSize() {
        return file.getSize();
    }

    @Nonnull
    @Override
    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }
}
