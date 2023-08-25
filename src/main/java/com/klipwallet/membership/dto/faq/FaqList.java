package com.klipwallet.membership.dto.faq;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;


@Schema(description = "FAQ 목록 DTO", accessMode = AccessMode.READ_ONLY)
public record FaqList (

    @Schema(description = "item list")
    List<FaqRow> items,
    @Schema(description = "total faq", example = "1")
    Long totalItem,
    @Schema(description = "total page", example = "1")
    Integer totalPage
){}
