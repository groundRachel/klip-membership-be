package com.klipwallet.membership.dto.partner.application;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import com.klipwallet.membership.dto.PhoneNumber;
import com.klipwallet.membership.dto.klipdrops.KlipDropsDto;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.entity.SignUpStatus;

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
    ) {
    }

    @Schema(description = "[TOOL] 가입 상태 조회 응답 DTO", accessMode = AccessMode.READ_ONLY)
    public record SignUpStatusResult(
            @NonNull @Schema(requiredMode = RequiredMode.REQUIRED) SignUpStatus status
    ) {
    }

    @Schema(description = """
                          [ADMIN] 파트너 신청, 거절 목록 조회를 위한 DTO
                          <신청 리스트>: id, 파트너명, 파트너 ID, 신청일시
                          <거절 리스트>: id, 파트너명, 거절일시, 검토자""", accessMode = AccessMode.READ_ONLY)
    public record PartnerApplicationRow(
            @NonNull Integer id,
            @NonNull String businessName,
            Integer klipDropsPartnerId,
            OffsetDateTime createdAt,
            OffsetDateTime processedAt,
            MemberSummary processor
    ) {
    }

    @Schema(description = "[ADMIN] 파트너 신청, 거절 상세 조회를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record PartnerApplicationDetail(
            @Schema(description = "파트너 신청 ID", requiredMode = RequiredMode.REQUIRED) @NonNull Integer id,
            @Schema(description = "파트너 사업자 이름", requiredMode = RequiredMode.REQUIRED) @NonNull String businessName,
            @Schema(description = "파트너 사업자 번호", requiredMode = RequiredMode.REQUIRED) @NonNull String businessRegistrationNumber,
            @Schema(description = "파트너 가입 요청 상태", requiredMode = RequiredMode.REQUIRED) Status status,
            @Schema(description = "가입 요청한 이메일 주소", requiredMode = RequiredMode.REQUIRED) @NonNull String email,
            @Schema(description = "가입 신청 시각", requiredMode = RequiredMode.REQUIRED) @NonNull OffsetDateTime appliedAt,

            @Schema(description = "Klip Drops Partner ID", requiredMode = RequiredMode.REQUIRED) @NonNull KlipDropsDto.PartnerDetail klipDropsDetail,
            @Schema(description = "가입 거절 정보") RejectDetail rejectDetail
    ) {
    }

    public record RejectDetail(
            @Schema(description = "가입 거절 시각") OffsetDateTime rejectedAt,
            @Schema(description = "가입 거절한 Admin") MemberSummary rejectedBy,
            @Schema(description = "가입 거절한 이유") String rejectReason
    ) {}

    @Schema(description = "[ADMIN] 파트너 신청, 거절 수를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record PartnerApplicationCount(
            @NonNull Long count
    ) {
    }

    @Schema(description = "[ADMIN] 파트너 신청 거절 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record RejectRequest(
            @Schema(requiredMode = RequiredMode.REQUIRED) String rejectReason
    ) {}

    @Schema(description = "[ADMIN] 파트너의 Klip Drops Partner ID 변경", accessMode = AccessMode.WRITE_ONLY)
    public record UpdateKlipDrops(
            @Schema(requiredMode = RequiredMode.REQUIRED) Integer partnerId
    ) {}
}
