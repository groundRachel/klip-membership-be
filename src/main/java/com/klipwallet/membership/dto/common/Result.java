package com.klipwallet.membership.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;


@Schema(description = "처리 결과 DTO", accessMode = AccessMode.READ_ONLY)
public record Result(@Schema(description = "result", example = "success") Status result) {
}

