package com.klipwallet.membership.controller.admin;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.service.PartnerService;

@Tag(name = "Admin.Partners", description = "Admin의 파트너 관리 API")
@RestController
@RequestMapping("/admin/v1/partners")
@RequiredArgsConstructor
public class PartnerAdminController {
    private final PartnerService partnerService;

    @Operation(summary = "가입한 파트너 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "파트너 목록 조회 권한 없음", content = @Content(schema = @Schema(ref = "Error403")))
    })
    @GetMapping
    public List<ApprovedPartnerDto> getPartners() {
        return partnerService.getPartners();
    }
}
