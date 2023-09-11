package com.klipwallet.membership.dto.klipdrops;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KlipDropsDto {

    @Schema(description = "[Admin] Klip Drops 파트너 목록 조회", accessMode = AccessMode.READ_ONLY)
    public record Partner(@NonNull Integer partnerId,
                          String partnerName,
                          String businessRegistrationNumber) {
    }
}
