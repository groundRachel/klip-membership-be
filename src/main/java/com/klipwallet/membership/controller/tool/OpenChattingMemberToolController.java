package com.klipwallet.membership.controller.tool;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.openchatting.OpenChattingMemberCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingMemberSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.OpenChattingMemberService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.OpenChattingMember", description = "Tool 채팅방 멤버 API")
@RestController
@RequestMapping("/tool/v1/openchattings/{openChattingId}/members")
@RequiredArgsConstructor
@Slf4j
public class OpenChattingMemberToolController {
    private final OpenChattingMemberService openChattingMemberService;

    @Operation(summary = "채팅방 멤버 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public OpenChattingMemberSummary createOpenChattingMember(
            @Valid @RequestBody OpenChattingMemberCreate command,
            @AuthenticationPrincipal AuthenticatedUser member) {
        return openChattingMemberService.create(command);
    }


}
