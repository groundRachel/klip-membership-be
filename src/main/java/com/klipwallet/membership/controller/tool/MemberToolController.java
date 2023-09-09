package com.klipwallet.membership.controller.tool;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.member.AuthenticationService;
import com.klipwallet.membership.dto.member.MemberAuthentication;
import com.klipwallet.membership.entity.AuthenticatedUser;

@Tag(name = "Tool.Member", description = "Tool 인증과 권한에 관한 API")
@RestController
@RequestMapping("/tool/v1/members")
@RequiredArgsConstructor
public class MemberToolController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "현재 접속자에 대한 정보 제공")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "접속자 정보 조회 성공. 로그인 하지 않아도 조회 가능.")
    })
    @GetMapping("/me")
    public MemberAuthentication authentication(@AuthenticationPrincipal AuthenticatedUser user) {
        return authenticationService.getMemberAuthentication(user);
    }
}
