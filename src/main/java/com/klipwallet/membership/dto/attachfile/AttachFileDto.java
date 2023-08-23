package com.klipwallet.membership.dto.attachfile;

import org.springframework.http.MediaType;

import com.klipwallet.membership.repository.AttachFile;

public class AttachFileDto {
    public record Meta(String attachFileId,
                       String linkUrl,
                       MediaType contentType,
                       Long contentLength
    ) {
        public Meta(AttachFile entity) {
            this(entity.getId(), entity.getLinkUrl(), entity.getContentType(), entity.getContentLength());
        }
    }
}
