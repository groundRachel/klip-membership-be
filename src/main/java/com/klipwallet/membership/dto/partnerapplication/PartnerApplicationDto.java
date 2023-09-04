package com.klipwallet.membership.dto.partnerapplication;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import com.klipwallet.membership.dto.PhoneNumber;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.PartnerApplication;

@RequiredArgsConstructor
public class PartnerApplicationDto {
    @Schema(description = "[TOOL] 파트너 신청 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record Application(@NotBlank String name,
                              @NotBlank @PhoneNumber String phoneNumber,
                              // TODO add validation; requirement from design team
                              @NotBlank @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$") String businessRegistrationNumber) {
        public PartnerApplication toPartnerApplication(AuthenticatedUser user) {
            return new PartnerApplication(name, phoneNumber, businessRegistrationNumber, user.getEmail(), user.getName());
        }
    }

    @Schema(description = "[TOOL] 파트너 신청 후 응답 DTO", accessMode = AccessMode.READ_ONLY)
    public record ApplyResult(
            @NonNull Integer id,
            @NonNull String name,
            OffsetDateTime createdAt
    ) {}

    @Schema(description = "[ADMIN] 파트너 신청 목록 조회를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record PartnerApplicationRow(
            @NonNull Integer id,
            @NonNull String businessName,
            Integer partnerId, // TODO fetch info from drops
            OffsetDateTime createdAt,
            OffsetDateTime processedAt,
            MemberSummary processor
    ) {}

    @Schema(description = "[ADMIN] 파트너 신청 거절 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record RejectRequest(
            String rejectReason
    ) {}
}
