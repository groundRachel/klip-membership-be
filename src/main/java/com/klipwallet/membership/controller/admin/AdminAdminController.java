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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.admin.AdminDto;
import com.klipwallet.membership.dto.admin.AdminDto.Row;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.AdminService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Tag(name = "Admin.Admin", description = "Admin 어드민 API")
@RestController
@RequestMapping("/admin/v1/admins")
@RequiredArgsConstructor
public class AdminAdminController {
    private final AdminService adminService;

    @Operation(summary = "Admin 어드민 등록.", description = "등록된 어드민이 인증 전까지는 유효한 상태가 아님")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "관리자 등록 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid RequestBody", content = @Content(schema = @Schema(ref = "Error400Fields"))),
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public AdminDto.Summary register(
            @Valid @RequestBody AdminDto.Register command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return adminService.register(command, user);
    }

    @Operation(summary = "Admin 어드민 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "어드민 목록 조회 성공")
    })
    @GetMapping
    public List<Row> list() {
        return adminService.getList();
    }

    @Operation(summary = "Admin 어드민 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "어드민 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 어드민", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @GetMapping("/{adminId}")
    public AdminDto.Detail detail(
            @Parameter(description = "어드민 id", required = true, example = "2") @PathVariable Integer adminId) {
        return adminService.getDetail(adminId);
    }

    @Operation(summary = "Admin 어드민 탈퇴", description = "논리적 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "어드민 탈퇴됨")
    })
    @DeleteMapping("/{adminId}")
    @ResponseStatus(NO_CONTENT)
    public void delete(
            @Parameter(description = "어드민 id", required = true, example = "2") @PathVariable Integer adminId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        adminService.withdraw(adminId, user);
    }
}
