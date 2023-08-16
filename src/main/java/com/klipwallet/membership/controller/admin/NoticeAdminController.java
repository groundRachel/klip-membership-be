package com.klipwallet.membership.controller.admin;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.notice.NoticeDto;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.NoticeService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Admin.Notice", description = "Admin 공지사항 API")
@RestController
@RequestMapping("/admin/v1/notices")
@RequiredArgsConstructor
public class NoticeAdminController {
    private final NoticeService noticeService;

    @Operation(summary = "공지사항 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공지사항 작성 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403", description = "공지사항 작성 권한 없음", content = @Content(schema = @Schema(ref = "Error403"))),
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public NoticeDto.Summary create(
            @Valid @RequestBody NoticeDto.Create command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return noticeService.create(command, user);
    }

    @Operation(summary = "공지사항 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403", description = "공지사항 작성 권한 없음", content = @Content(schema = @Schema(ref = "Error403"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PutMapping("/{noticeId}")
    public NoticeDto.Detail update(
            @Parameter(description = "공지사항 id", required = true, example = "2") @PathVariable Integer noticeId,
            @Valid @RequestBody NoticeDto.Update command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return noticeService.update(noticeId, command, user);
    }
}
