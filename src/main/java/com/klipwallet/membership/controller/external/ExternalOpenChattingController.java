package com.klipwallet.membership.controller.external;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.openchatting.OpenChattingMemberCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingStatus;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.service.OpenChattingService;

@Tag(name = "External.OpenChatting", description = "External 채팅방 API")
@RestController
@RequestMapping("/external/v1/open-chattings")
@RequiredArgsConstructor
@Slf4j
public class ExternalOpenChattingController {
    private final OpenChattingService openChattingService;

    @Operation(summary = "채팅방 참여하기")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "참여 성공"),
            @ApiResponse(responseCode = "403", description = "채팅방 참여 권한 없음",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "채팅방 없음",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/contracts/{sca}/tokens/{tokenId}")
    @ResponseStatus(HttpStatus.CREATED)
    public OpenChattingStatus joinChat(
            @Parameter(description = "contract address", required = true,
                       example = "0xa9a95c5fef43830d5d67156a2582a2e793acb465") @PathVariable Address sca,
            @Parameter(description = "token id", required = true, example = "35100240011") @PathVariable TokenId tokenId,
            @Valid @RequestBody OpenChattingMemberCreate command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return openChattingService.joinChat(sca, tokenId, command, user);
    }

    @Operation(summary = "채팅방 조회")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "조회 성공")})
    @GetMapping("/contracts/{sca}/tokens/{tokenId}")
    public OpenChattingStatus getOpenChattingStatus(
            @Parameter(description = "contract address", required = true,
                       example = "0xa9a95c5fef43830d5d67156a2582a2e793acb465") @PathVariable Address sca,
            @Parameter(description = "token id", required = true, example = "35100240011") @PathVariable TokenId tokenId) {
        return openChattingService.getOpenChattingStatus(sca, tokenId);
    }
}