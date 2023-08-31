package com.klipwallet.membership.dto.attachfile;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import org.springframework.http.MediaType;
import org.springframework.util.unit.DataSize;

import com.klipwallet.membership.entity.AttachFile;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public class AttachFileDto {
    @Schema(description = "첨부파일 메타 데이타 DTO", accessMode = AccessMode.READ_ONLY)
    public record Metadata(
            @Schema(description = "첨부파일", format = "UUID", requiredMode = REQUIRED, example = "fab3123f-c36a-47c2-9b8a-a5e6a3684952")
            String attachFileId,
            @Schema(description = "Link URL", format = "url", requiredMode = REQUIRED, example = "fab3123f-c36a-47c2-9b8a-a5e6a3684952")
            String linkUrl,
            @Schema(description = "Content-Type", type = "string", requiredMode = REQUIRED, example = "image/png")
            MediaType contentType,
            @Schema(description = "Byte 사이즈", requiredMode = REQUIRED, example = "33102113")
            DataSize contentLength
    ) {
        public Metadata(AttachFile entity) {
            this(entity.getId(), entity.getLinkUrl(), entity.getContentType(), entity.getContentLength());
        }
    }
}
