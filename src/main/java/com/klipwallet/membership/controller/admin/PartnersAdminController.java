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
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.AppliedPartnersResult;
import com.klipwallet.membership.service.PartnerService;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "Admin.Partners", description = "Admin의 파트너 관리 API")
@RestController
@RequestMapping("/admin/v1/partners")
@RequiredArgsConstructor
public class PartnersAdminController {
    private final PartnerService partnerService;

    @Operation(summary = "가입 요청한 파트너 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/applied")
    public List<AppliedPartnersResult> getAppliedPartners() {
        return partnerService.getAppliedPartners();
    }

    @Operation(summary = "가입한 파트너 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/accepted")
    public List<PartnerDto.AcceptedPartnersResult> getAcceptedPartners() {
        return partnerService.getAcceptedPartners();
    }

    @Operation(summary = "요청한 파트너 승인 또는 거절")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "반영 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "승인 권한 없음 or 카카오 연동 안됨",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/accept")
    @ResponseStatus(OK)
    public PartnerDto.AcceptResult acceptResult(@RequestBody @Valid PartnerDto.AcceptRequest body) throws Exception {
        return partnerService.acceptPartner(body);
    }
}
