package com.klipwallet.membership.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.NonNull;

import com.klipwallet.membership.entity.MemberId;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "멤버 요약 정보", accessMode = AccessMode.READ_ONLY)
public record MemberSummary(@Schema(description = "멤버 아이디", requiredMode = REQUIRED, type = "int32", example = "1") @NonNull MemberId id,
                            @Schema(description = "멤버 이름", requiredMode = REQUIRED, example = "정조던") @NonNull String name) {
    public static MemberSummary deactivated(MemberId memberId) {
        // TODO FIXME
        return new MemberSummary(memberId, "Deactivated");
    }
}
