package com.klipwallet.membership.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.OperatorInvitable;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Tag(name = "Local.Common", description = "Local Helper API")
@Profile({"local", "local-dev"})
@Controller
@RequiredArgsConstructor
public class LocalController {
    private final OperatorInvitable operatorInvitable;

    @Operation(summary = "Tool 운영진 초대", description = "local 환경에서 편의성 용도로만 사용할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "운영진 초대 URL 반환"),
    })
    @PostMapping("/tool/v1/operators/invite-local")
    @ResponseBody
    public InviteResult inviteOperator(@RequestParam String phone, @AuthenticationPrincipal AuthenticatedUser partner) {
        String invitationUrl = operatorInvitable.inviteOperator(partner.getMemberId(), phone);
        return new InviteResult(invitationUrl);
    }

    @Schema(description = "운영진 초대 응답 DTO", accessMode = AccessMode.WRITE_ONLY)
    @SuppressWarnings("HttpUrlsUsage")
    public record InviteResult(
            @Schema(description = "운영진 초대 링크", requiredMode = REQUIRED,
                    example = "http://membership.local.com:3000/landing/invite-operator?code=5620810961087251174")
            @JsonProperty("invitationUrl") String invitationUrl) {
    }
}
