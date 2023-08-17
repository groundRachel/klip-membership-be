package com.klipwallet.membership.dto.faq;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.NonNull;

import com.klipwallet.membership.entity.Faq;

@Schema(description = "FQA 요약 DTO", accessMode = AccessMode.READ_ONLY)
public record FaqSummary(@NonNull @Schema(description = "FAQ ID", example = "1") Integer id) {
    public FaqSummary(Faq saved) {
        this(saved.getId());
    }
}