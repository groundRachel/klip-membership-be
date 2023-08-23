package com.klipwallet.membership.dto.attachfile;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import org.springframework.http.MediaType;

import com.klipwallet.membership.repository.AttachFile;

public class AttachFileDto {
    @Schema(description = "첨부파일 메타 DTO", accessMode = AccessMode.READ_ONLY)
    public record Meta(
            @Schema(description = "첨부파일", format = "UUID", example = "fab3123f-c36a-47c2-9b8a-a5e6a3684952")
            String attachFileId,
            @Schema(description = "Link URL", format = "url", example = "fab3123f-c36a-47c2-9b8a-a5e6a3684952")
            String linkUrl,
            @Schema(description = "Content-Type", type = "string", example = "image/png")
            MediaType contentType,
            @Schema(description = "Byte 사이즈", example = "33102113")
            Long contentLength
    ) {
        public Meta(AttachFile entity) {
            this(entity.getId(), entity.getLinkUrl(), entity.getContentType(), entity.getContentLength());
        }
    }
}
