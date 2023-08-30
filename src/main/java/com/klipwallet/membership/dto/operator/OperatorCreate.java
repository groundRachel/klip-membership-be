package com.klipwallet.membership.dto.operator;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Operator;

@Schema(description = "멤버쉽 관리자 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record OperatorCreate(
        @Schema(description = "클립 회원 Id")
        @NotNull
        String klipRequestKey
) {
    @JsonIgnore
    public Operator toOperator(Long klipId, String kakaoUserId, Integer parterId, MemberId memberId) {
        return new Operator(klipId, kakaoUserId, parterId, memberId);
    }
}
