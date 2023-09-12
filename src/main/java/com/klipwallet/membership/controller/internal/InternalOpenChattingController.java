package com.klipwallet.membership.controller.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.openchatting.OpenChattingStatus;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.service.OpenChattingService;

@Tag(name = "Internal.OpenChatting", description = "Internal 채팅방 API")
@RestController
@RequestMapping("/internal/v1/openchattings")
@RequiredArgsConstructor
@Slf4j
public class InternalOpenChattingController {
    private final OpenChattingService openChattingService;

    @Operation(summary = "채팅방 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "채팅방 조회 권한 없음",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/contract/{sca}/token/{tokenId}")
    public OpenChattingStatus getOpenChattingStatus(
            @Parameter(description = "contract address", required = true, example = "0xa9a95c5fef43830d5d67156a2582a2e793acb465") @PathVariable Address sca,
            @Parameter(description = "token id", required = true, example = "35100240011") @PathVariable TokenId tokenId,
            @Parameter(description = "klaytnAddress", required = true, example = "0xa9a95c5fef43830d5d67156a2582a2e793acb465") @RequestParam Address klaytnAddress) {
        return openChattingService.getOpenChattingStatusByKlaytnAddress(sca, tokenId, klaytnAddress);
    }
}