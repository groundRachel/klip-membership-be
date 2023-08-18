package com.klipwallet.membership.dto.member;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.entity.MemberId;

@RequiredArgsConstructor
public class PartnerDto {
    @Schema(description = "[TOOL] 파트너 신청 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record Application(@NotBlank String name,
                              // TODO add validation; requirement from design team
                              @NotBlank @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$") String phoneNumber,
                              // TODO add validation; requirement from design team
                              @NotBlank @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$") String businessRegistrationNumber,
                              @NotBlank @Email String email,
                              @NotBlank String oAuthId) {
        public AppliedPartner toAppliedPartner() {
            return new AppliedPartner(name, phoneNumber, businessRegistrationNumber, email, oAuthId);
        }
    }

    @Schema(description = "[TOOL] 파트너 신청 후 응답 DTO", accessMode = AccessMode.READ_ONLY)
    public record ApplyResult(
            @NonNull MemberId id,
            @NonNull String name,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}

    @Schema(description = "[ADMIN] 파트너 신청 목록 조회를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record AppliedPartnerDto(
            @NonNull MemberId id,
            @NonNull String name,
            OffsetDateTime createdAt,
            Status status,
            String rejectReason
    ) {}

    @Schema(description = "[ADMIN] 가입한 파트너 목록 조회를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record AcceptedPartnerDto(
            @NonNull MemberId id,
            @NonNull String name,
            OffsetDateTime createdAt
    ) {}

    @Schema(description = "[ADMIN] 파트너 신청 승인 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record ApproveRequest(
            @NonNull MemberId id
    ) {}

    @Schema(description = "[ADMIN] 파트너 신청 거절 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record RejectRequest(
            @NonNull MemberId id,
            String rejectReason
    ) {}
}
