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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.notice.NoticeDto;
import com.klipwallet.membership.dto.notice.NoticeDto.Row;
import com.klipwallet.membership.entity.ArticleStatus;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.NoticeService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Tag(name = "Admin.Notice", description = "Admin 공지사항 API")
@RestController
@RequestMapping("/admin/v1/notices")
@RequiredArgsConstructor
public class NoticeAdminController {
    private final NoticeService noticeService;

    @Operation(summary = "Admin 공지사항 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공지사항 작성 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid RequestBody", content = @Content(schema = @Schema(ref = "Error400Fields"))),
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public NoticeDto.Summary create(
            @Valid @RequestBody NoticeDto.Create command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return noticeService.create(command, user);
    }

    @Operation(summary = "Admin 공지사항 상태 별 목록 조회",
               description = """
                             - status=live: 최근 Live일시 최신 순으로 정렬되며, **고정 공지 미노출**
                             - status!=live: 최근 수정일시 최신 순으로 정렬되며, 고정 공지 노출
                             """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid Query", content = @Content(schema = @Schema(ref = "Error400"))),
    })
    @GetMapping
    public Page<Row> list(
            @Parameter(description = "필터링 할 공지 상태", required = true, example = "draft") @RequestParam("status") ArticleStatus status,
            @ParameterObject Pageable pageable) {
        return noticeService.getListByStatus(status, pageable);
    }

    @Operation(summary = "Admin 공지사항 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 공지사항", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @GetMapping("/{noticeId}")
    public NoticeDto.Detail detail(
            @Parameter(description = "공지사항 id", required = true, example = "2") @PathVariable Integer noticeId) {
        return noticeService.getDetail(noticeId);
    }

    @Operation(summary = "Admin 고정 공지사항 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "고정 공지 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 고정 공지", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @GetMapping("/primary")
    public NoticeDto.Row primary() {
        return noticeService.getPrimaryNotice();
    }

    @Operation(summary = "Admin 공지사항 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid RequestBody or Query", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 공지사항", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PutMapping("/{noticeId}")
    public NoticeDto.Detail update(
            @Parameter(description = "공지사항 id", required = true, example = "2") @PathVariable Integer noticeId,
            @Valid @RequestBody NoticeDto.Update command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return noticeService.update(noticeId, command, user);
    }

    @Operation(summary = "Admin 공지사항 상태 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid RequestBody or Query", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 공지사항", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PutMapping("/{noticeId}/status")
    public NoticeDto.Status changeStatus(
            @Parameter(description = "공지사항 id", required = true, example = "2") @PathVariable Integer noticeId,
            @Valid @RequestBody NoticeDto.Status command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return noticeService.changeStatus(noticeId, command, user);
    }

    @Operation(summary = "Admin 공지사항 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "공지사항 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid Query", content = @Content(schema = @Schema(ref = "Error400")))
    })
    @DeleteMapping("/{noticeId}")
    @ResponseStatus(NO_CONTENT)
    public void delete(
            @Parameter(description = "공지사항 id", required = true, example = "2") @PathVariable Integer noticeId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        noticeService.delete(noticeId, user);
    }
}
