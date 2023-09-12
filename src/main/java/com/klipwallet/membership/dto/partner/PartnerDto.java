package com.klipwallet.membership.dto.partner;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import com.klipwallet.membership.dto.PhoneNumber;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.MemberId;

@RequiredArgsConstructor
public class PartnerDto {
    @Schema(description = "[ADMIN] 가입한 파트너 목록 조회를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record ApprovedPartnerDto(
            @NonNull MemberId id,
            @NonNull String name,

            OffsetDateTime processedAt,
            MemberSummary processor

            // TODO 오픈채팅 운영 여부 정보 포함하기
    ) {}

    @Schema(description = "[Tool] 파트너 마이페이지 - 내 정보 조회를 위한 Dto", accessMode = AccessMode.READ_ONLY)
    public record DetailByTool(
            @NonNull @Schema(description = "사업자 이름", example = "(주) 그라운드엑스", requiredMode = RequiredMode.REQUIRED) String name,
            @NonNull @Schema(description = "사업자 번호", example = "000-00-00000",
                             requiredMode = RequiredMode.REQUIRED) String businessRegistrationNumber,
            @NonNull @Schema(description = "담당자 전화번호", example = "010-1234-5678", requiredMode = RequiredMode.REQUIRED) String phoneNumber
    ) {}

    @Schema(description = "[Tool] 파트너 마이페이지 - 내 정보 수정을 위한 Dto", accessMode = AccessMode.WRITE_ONLY)
    public record Update(
            @NotBlank @Schema(description = "사업자 이름", example = "(주) 그라운드엑스", requiredMode = RequiredMode.REQUIRED) String name,
            @NotBlank @Schema(description = "담당자 전화번호", example = "010-1234-5678",
                              requiredMode = RequiredMode.REQUIRED) @PhoneNumber String phoneNumber) {
    }

    @Schema(description = "[ADMIN] 파트너 상세 조회를 위한 DTO", accessMode = AccessMode.READ_ONLY)
    public record DetailByAdmin(
            MemberId id,
            String businessName,
            String businessRegistrationNumber,
            String email,
            OffsetDateTime appliedAt,

            Integer klipDropsPartnerId,
            ApproveDetail approveDetail
            // TODO add open chatting info
    ) {
    }

    public record ApproveDetail(
            MemberSummary approvedBy,
            OffsetDateTime approvedAt
    ) {
    }
}
