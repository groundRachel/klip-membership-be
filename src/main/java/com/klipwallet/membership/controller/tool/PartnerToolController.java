package com.klipwallet.membership.controller.tool;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.partner.PartnerDto;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.PartnerService;

@Tag(name = "Tool.Partner", description = "파트너 관리 API")
@RestController
@RequestMapping("/tool/v1/partner")
@RequiredArgsConstructor
public class PartnerToolController {
    private final PartnerService partnerService;

    @Operation(summary = "파트너 상세 정보 조회 (마이페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파트너 상세 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 파트너", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @GetMapping
    public PartnerDto.Detail detail(@AuthenticationPrincipal AuthenticatedUser user) {
        return partnerService.getDetail(user.getMemberId());
    }

    @Operation(summary = "파트너 상세 정보 수정 (마이페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파트너 상세 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid RequestBody or Query", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 파트너", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PutMapping
    public PartnerDto.Detail update(
            @Valid @RequestBody PartnerDto.Update command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return partnerService.update(command, user.getMemberId());
    }
}
