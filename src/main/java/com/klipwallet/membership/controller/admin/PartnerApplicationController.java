package com.klipwallet.membership.controller.admin;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.member.PartnerDto.AppliedPartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.ApproveRequest;
import com.klipwallet.membership.dto.member.PartnerDto.RejectRequest;
import com.klipwallet.membership.service.PartnerService;

@Tag(name = "Admin.Partner-Application", description = "Admin의 파트너 가입 요청 관리 API")
@RestController
@RequestMapping("/admin/v1/partner-applications")
@RequiredArgsConstructor
public class PartnerApplicationController {
    private final PartnerService partnerService;

    @Operation(summary = "가입 요청한 파트너 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "파트너 요청 목록 조회 권한 없음", content = @Content(schema = @Schema(ref = "Error403")))
    })
    @GetMapping("/applied")
    public List<AppliedPartnerDto> getAppliedPartners() {
        return partnerService.getAppliedPartners();
    }

    @Operation(summary = "요청한 파트너 승인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403", description = "파트너 승인 권한 없음", content = @Content(schema = @Schema(ref = "Error403"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PostMapping("/approve")
    public void approvePartner(@RequestBody @Valid ApproveRequest body) throws Exception {
        partnerService.approve(body);
    }

    @Operation(summary = "요청한 파트너 거절")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403", description = "파트너 거절 권한 없음", content = @Content(schema = @Schema(ref = "Error403"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PostMapping("/reject")
    public void rejectPartner(@RequestBody @Valid RejectRequest body) throws Exception {
        partnerService.reject(body);
    }
}
