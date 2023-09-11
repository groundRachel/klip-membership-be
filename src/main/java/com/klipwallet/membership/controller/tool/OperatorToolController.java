package com.klipwallet.membership.controller.tool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.PhoneNumber;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.OperatorService;

@Tag(name = "Tool.Operator", description = "Tool 운영진 API")
@RestController
@RequestMapping("/tool/v1/operators")
@RequiredArgsConstructor
public class OperatorToolController {
    private final OperatorService operatorService;

    @Operation(summary = "Tool 운영진 초대")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    @PostMapping("/invite")
    @ResponseBody
    public void inviteOperator(@RequestParam @PhoneNumber String phone, @AuthenticationPrincipal AuthenticatedUser partner) {
        operatorService.inviteOperator(partner.getMemberId(), phone);
    }
}
