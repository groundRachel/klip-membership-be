package com.klipwallet.membership.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.admin.AdminDto.Profile;
import com.klipwallet.membership.entity.AuthenticatedUser;

@Tag(name = "Admin.AdminProfile", description = "Admin 프로필 정보 관리 API")
@RestController
@RequestMapping("/admin/v1/profiles")
@RequiredArgsConstructor
public class AdminProfileAdminController {

    @Operation(summary = "Admin의 기본 정보 조회", description = "구글 로그인 직후, 기본 정보를 조회하기 위함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public Profile getProfile(AuthenticatedUser user) {
        return new Profile(user.getEmail());
    }
}
