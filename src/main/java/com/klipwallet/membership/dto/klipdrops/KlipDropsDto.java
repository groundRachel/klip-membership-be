package com.klipwallet.membership.dto.klipdrops;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KlipDropsDto {

    @Schema(description = "[Admin] Klip Drops 파트너 목록 조회", accessMode = AccessMode.READ_ONLY)
    public record Partner(
            @Schema(description = "Klip Drops Partner ID", requiredMode = RequiredMode.REQUIRED) @NonNull Integer partnerId,
            @Schema(description = "Klip Drops Partner의 이름", requiredMode = RequiredMode.REQUIRED) @NonNull String partnerName,
            @Schema(description = "Klip Drops Partner의 사업자 번호", requiredMode = RequiredMode.REQUIRED) @NonNull String businessRegistrationNumber) {
    }

    @Schema(description = "파트너 요청 상세 조회 > Klip Drops 파트너 조회", accessMode = AccessMode.READ_ONLY)
    public record PartnerDetail(
            @Schema(description = "Klip Drops Partner ID") Integer partnerId,
            @Schema(description = "Klip Drops Partner의 이름") String partnerName) {
    }
}
