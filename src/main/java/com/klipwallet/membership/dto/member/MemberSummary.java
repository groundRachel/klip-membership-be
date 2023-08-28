package com.klipwallet.membership.dto.member;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.NonNull;

import com.klipwallet.membership.entity.Member;
import com.klipwallet.membership.entity.MemberId;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "멤버 요약 정보", accessMode = AccessMode.READ_ONLY)
public record MemberSummary(
        @Schema(description = "멤버 아이디", type = "integer", format = "int32", requiredMode = RequiredMode.REQUIRED, example = "1")
        @NonNull
        MemberId id,
        @Schema(description = "멤버 이름", requiredMode = RequiredMode.REQUIRED, example = "jordan.jung")
        @NonNull
        String name) {
    public MemberSummary(Member entity) {
        this(Objects.requireNonNull(entity.getMemberId()), entity.getName());
    }

    public static MemberSummary deactivated(MemberId memberId) {
        // TODO FIXME
        return new MemberSummary(memberId, "Deactivated");
    }
}
