package com.klipwallet.membership.controller.admin;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.klipdrops.KlipDropsDto;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.PartnerApplicationCount;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.PartnerApplicationRow;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.RejectRequest;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.service.PartnerApplicationService;

@Tag(name = "Admin.PartnerApplication", description = "Admin의 파트너 가입 요청 관리 API")
@RestController
@RequestMapping("/admin/v1/partner-applications")
@RequiredArgsConstructor
public class PartnerApplicationAdminController {
    private final PartnerApplicationService partnerApplicationService;

    @Operation(summary = "파트너 가입 요청 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping
    public List<PartnerApplicationRow> getPartnerApplications(@ParameterObject Pageable page,
                                                              @RequestParam Status status) {
        return partnerApplicationService.getPartnerApplications(page, status);
    }

    @Operation(summary = "파트너 가입 요청, 거절 수 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/count")
    public PartnerApplicationCount getPartnerApplicationNumber(@RequestParam Status status) {
        return partnerApplicationService.getPartnerApplicationNumber(status);
    }

    @Operation(summary = "Klip Drops 파트너 목록 조회",
               description = "Klip Drops 파트너 ID 변경을 위한 목록 조회. 현재 prod 환경은 partner 수가 200개 미만임. 따라서 1회로 모든 데이터를 조회함.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/klipdrops/partners")
    public List<KlipDropsDto.Partner> getKlipDropsPartners(@RequestParam(required = false) String search) {
        return partnerApplicationService.getKlipDropsPartners(search);
    }

    @Operation(summary = "가입 요청서의 Klip Drops 파트너 ID 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "가입 요청의 사업자 번호와 Klip Drops 파트너의 사업자 번호가 일치하지 않음",
                         content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너 가입 요청 또는 파트너 ID", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PatchMapping("{partnerApplicationId}/klipdrops/partners/{klipDropsPartnerId}")
    public void changeKlipDropsPartnerId(
            @Parameter(description = "변경 할 가입 요청 ID", required = true, example = "3") @PathVariable Integer partnerApplicationId,
            @Parameter(description = "Klip Drops Partner ID", required = true, example = "1") @PathVariable Integer klipDropsPartnerId) {
        partnerApplicationService.updateKlipDropsPartnerId(partnerApplicationId, klipDropsPartnerId);
    }

    @Operation(summary = "요청한 파트너 승인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너 가입 요청", content = @Content(schema = @Schema(ref = "Error404"))),
            @ApiResponse(responseCode = "409", description = "서버 상태와 충돌", content = @Content(schema = @Schema(ref = "Error409")))
    })
    @PostMapping("/{applicationId}/approve")
    public void approvePartner(
            @Parameter(description = "승인 할 파트너 요청 Id", required = true, example = "3") @PathVariable Integer applicationId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        partnerApplicationService.approve(applicationId, user);
    }

    @Operation(summary = "요청한 파트너 거절")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너 가입 요청", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PostMapping("/{applicationId}/reject")
    public void rejectPartner(@Parameter(description = "거절 할 파트너 요청 Id", required = true, example = "3") @PathVariable Integer applicationId,
                              @RequestBody @Valid RejectRequest body,
                              @AuthenticationPrincipal AuthenticatedUser user) {
        partnerApplicationService.reject(applicationId, body, user);
    }
}
