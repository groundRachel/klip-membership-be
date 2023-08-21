package com.klipwallet.membership.dto.partner;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import com.klipwallet.membership.entity.MemberId;

@RequiredArgsConstructor
public class PartnerDto {
    @Schema(description = "[ADMIN] 가입한 파트너 목록 조회를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record ApprovedPartnerDto(
            @NonNull MemberId id,
            @NonNull String name,
            OffsetDateTime createdAt
    ) {}
}
