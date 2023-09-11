package com.klipwallet.membership.controller.tool;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.openchatting.OpenChattingCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingDetail;
import com.klipwallet.membership.dto.openchatting.OpenChattingSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.OpenChatting.Status;
import com.klipwallet.membership.service.OpenChattingService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.OpenChatting", description = "Tool 채팅방 API")
@RestController
@RequestMapping("/tool/v1/open-chattings")
@RequiredArgsConstructor
@Slf4j
public class OpenChattingToolController {
    private final OpenChattingService openChattingService;

    @Operation(summary = "채팅방 개설")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "개설 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public OpenChattingSummary createOpenChatting(
            @Valid @RequestBody OpenChattingCreate command,
            @AuthenticationPrincipal AuthenticatedUser member) {
        return openChattingService.create(command, member);
    }

    @Operation(summary = "채팅방 목록 조회", description = """
                                                    - status 없을 경우 activated, deleted 채팅방 목록 전체 생성 일시 최신순으로 조회
                                                    - status=activated
                                                    - status=deleted
                                                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "invalid query", content = @Content(schema = @Schema(ref = "Error400"))),
    })
    @GetMapping
    public Page<OpenChattingSummary> openChattingList(
            @Parameter(description = "필터링 할 채팅방 상태", required = false, example = "activated")
            @RequestParam(name = "status", required = false) Status status,
            @ParameterObject Pageable pageable) {
        return openChattingService.list(status, pageable);
    }

    @Operation(summary = "채팅방 상세 조회", description = "조회 성공")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "invalid query", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "not found", content = @Content(schema = @Schema(ref = "Error404"))),
    })
    @GetMapping("/{openChattingId}")
    public OpenChattingDetail openChattingDetail(
            @Parameter(description = "오픈채팅방 id", required = true, example = "1")
            @PathVariable Long openChattingId) {
        return openChattingService.detail(openChattingId);
    }

}
