package com.klipwallet.membership.controller.external;

import jakarta.servlet.http.HttpSession;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.TokenId;

@Tag(name = "External.OpenChatting", description = "외부 오픈 채팅 API")
@RestController
@RequestMapping("/external/v1/open-chattings")
@RequiredArgsConstructor
public class OpenChattingExternalController {

    @Operation(summary = "External 오픈 채팅 참여")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "오픈 채팅 참여 성공"),
            @ApiResponse(responseCode = "400",
                         description = """
                                       - code: `400***`: 오픈 채팅 프로필이 존재하지 않음.
                                       - code: `400000`: Invalid Query
                                       """, content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403",
                         description = """
                                       - code: `403000`: 카카오 인증 권한이 없는 경우
                                       - code: `403***`: NFT 소유자가 아닌 경우
                                       """, content = @Content(schema = @Schema(ref = "Error403"))),
            @ApiResponse(responseCode = "404",
                         description = """
                                       - code: `404***`: 존재하지 않는 오픈 채팅
                                       """, content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PostMapping("/join")
    public Object joinChatting(@Schema(description = "NFT SCA", example = "0xa9a95c5fef43830d5d67156a2582a2e793acb465")
                               @RequestParam("sca") Address sca,
                               @Schema(description = "Token ID", example = "35100240002")
                               @RequestParam("tokenId") TokenId tokenId,
                               @AuthenticationPrincipal AuthenticatedUser user,
                               HttpSession session) {
        session.invalidate();
        return null;
    }
}
