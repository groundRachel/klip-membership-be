package com.klipwallet.membership.dto.faq;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.NonNull;

import com.klipwallet.membership.entity.ArticleStatus;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "FAQ 상태 DTO", accessMode = AccessMode.READ_WRITE)
public record FaqStatus(@NonNull @NotNull @JsonProperty("status") @Schema(requiredMode = REQUIRED) ArticleStatus status) {
}
