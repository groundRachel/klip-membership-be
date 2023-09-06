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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.PartnerApplicationService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.PartnerApplication", description = "파트너 가입 요청 API")
@RestController
@RequestMapping("/tool/v1/partner-applications")
@RequiredArgsConstructor
public class PartnerApplicationToolController {
    private final PartnerApplicationService partnerApplicationService;

    @Operation(summary = "파트너 가입 요청", description = "파트너가 구글 인증 후 가입 요청 보냄. GX의 admin이 승인을 해야 가입됨.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "409", description = "서버 상태와 충돌", content = @Content(schema = @Schema(ref = "Error409")))
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public PartnerApplicationDto.ApplyResult apply(
            @Valid @RequestBody PartnerApplicationDto.Application body,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return partnerApplicationService.apply(body, user);
    }

    @Operation(summary = "가입 상태 조회", description = "파트너가 구글 인증 직후 호출하는 API. 파트너 가입 여부를 알 수 있음.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/me")
    public PartnerApplicationDto.SignUpStatusResult getSignUpStatus(@AuthenticationPrincipal AuthenticatedUser user) {
        return partnerApplicationService.getSignUpStatus(user);
    }
}
