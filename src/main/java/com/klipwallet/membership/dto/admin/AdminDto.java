package com.klipwallet.membership.dto.admin;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.NonNull;

import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;

public class AdminDto {
    @Schema(description = "어드민 등록 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record Register(
            @Schema(description = "등록할 이메일", example = "jordan.jung@groundx.xyz")
            @NotNull @Email(regexp = ".*\\@groundx\\.xyz$", message = "{com.klipwallet.membership.dto.admin.AdminDto.Register.email.message}")
            String email) {

        @JsonIgnore
        public Admin toAdmin(AuthenticatedUser registrant) {
            return new Admin(email, registrant.getMemberId());
        }
    }

    @Schema(description = "어드민 요약 DTO", accessMode = AccessMode.READ_ONLY)
    public record Summary(
            @NonNull @Schema(description = "어드민 ID", type = "integer", format = "int32", requiredMode = RequiredMode.REQUIRED, example = "2")
            MemberId id,
            @NonNull @Schema(description = "이메일", requiredMode = RequiredMode.REQUIRED, example = "jordan.jung@groundx.xyz")
            String email) {
        @SuppressWarnings("DataFlowIssue")
        public Summary(Admin entity) {
            this(entity.getMemberId(), entity.getEmail());
        }
    }

    @Schema(description = "어드민 목록의 ROW DTO", accessMode = AccessMode.READ_ONLY)
    public record Row(
            @NonNull
            @Schema(description = "어드민 ID", type = "integer", format = "int32", requiredMode = RequiredMode.REQUIRED, example = "2") MemberId id,
            @NonNull
            @Schema(description = "이메일", requiredMode = RequiredMode.REQUIRED, example = "jordan.jung@groundx.xyz")
            String email,
            @NonNull
            @Schema(description = "추가 일시", requiredMode = RequiredMode.REQUIRED, example = "2023-07-24T15:38:24.005795+09:00")
            OffsetDateTime createdAt,
            @NonNull
            @Schema(description = "생성한 관리자", requiredMode = RequiredMode.REQUIRED)
            MemberSummary creator) {
    }

    @Schema(description = "어드민 상세 DTO", accessMode = AccessMode.READ_ONLY)
    public record Detail(
            @NonNull
            @Schema(description = "어드민 ID", type = "integer", format = "int32", requiredMode = RequiredMode.REQUIRED, example = "2") MemberId id,
            @NonNull
            @Schema(description = "이메일", requiredMode = RequiredMode.REQUIRED, example = "jordan.jung@groundx.xyz")
            String email,
            @NonNull
            @Schema(description = "이름", requiredMode = RequiredMode.REQUIRED, example = "jordan.jung")
            String name,
            @Schema(description = "OAuth2 ID", example = "1002048777740038310")
            String oAuthId,
            @NonNull
            @Schema(description = "추가 일시", requiredMode = RequiredMode.REQUIRED, example = "2023-07-24T15:38:24.005795+09:00")
            OffsetDateTime createdAt,
            @NonNull
            @Schema(description = "생성한 관리자", requiredMode = RequiredMode.REQUIRED)
            MemberSummary creator,
            @NonNull
            @Schema(description = "마지막 수정 일시", requiredMode = RequiredMode.REQUIRED, example = "2023-07-24T15:38:24.005795+09:00")
            OffsetDateTime updatedAt,
            @NonNull
            @Schema(description = "마지막 수정 관리자", requiredMode = RequiredMode.REQUIRED)
            MemberSummary updater) {
    }
}
