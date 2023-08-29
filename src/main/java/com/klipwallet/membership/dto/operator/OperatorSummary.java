package com.klipwallet.membership.dto.operator;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.NonNull;

import com.klipwallet.membership.entity.Operator;

@Schema(description = "운영자 요약 정보", accessMode = AccessMode.READ_ONLY)
public record OperatorSummary(
        @Schema(description = "운영자 ID", type = "string", requiredMode = RequiredMode.REQUIRED, example = "1")
        @NonNull
        Long id,
        @Schema(description = "운영자 Klip ID", type = "string", requiredMode = RequiredMode.REQUIRED, example = "1")
        @NonNull
        Long klipId
) {
    public OperatorSummary(Operator saved) {
        this(saved.getId(), saved.getKlipId());
    }
}
