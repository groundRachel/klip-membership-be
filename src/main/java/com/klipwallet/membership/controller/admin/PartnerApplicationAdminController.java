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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.PartnerApplicationRow;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.RejectRequest;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.exception.InvalidRequestException;
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
            @ApiResponse(responseCode = "403", description = "파트너 요청 목록 조회 권한 없음", content = @Content(schema = @Schema(ref = "Error403")))
    })
    @GetMapping
    public List<PartnerApplicationRow> getPartnerApplications(@ParameterObject Pageable page,
                                                              @RequestParam Status status) {
        if (status.getCode() == 0) {
            throw new InvalidRequestException();
        }
        return partnerApplicationService.getPartnerApplications(page, status);
    }

    @Operation(summary = "요청한 파트너 승인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403", description = "파트너 승인 권한 없음", content = @Content(schema = @Schema(ref = "Error403"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너", content = @Content(schema = @Schema(ref = "Error404")))
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
            @ApiResponse(responseCode = "403", description = "파트너 거절 권한 없음", content = @Content(schema = @Schema(ref = "Error403"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PostMapping("/{applicationId}/reject")
    public void rejectPartner(@Parameter(description = "거절 할 파트너 요청 Id", required = true, example = "3") @PathVariable Integer applicationId,
                              @RequestBody @Valid RejectRequest body,
                              @AuthenticationPrincipal AuthenticatedUser user) {
        partnerApplicationService.reject(applicationId, body, user);
    }
}
