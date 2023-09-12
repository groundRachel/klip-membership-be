package com.klipwallet.membership.controller.external;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klipwallet.membership.config.KlipMembershipProperties;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.TokenId;

@Tag(name = "External.OpenChatting", description = "외부 오픈 채팅 API")
@Controller
@RequestMapping("/external/v1/open-chattings")
@RequiredArgsConstructor
@Slf4j
public class OpenChattingExternalController {
    private final KlipMembershipProperties properties;
    private final SecurityContextRepository securityContextRepository;

    @Operation(summary = "모바일앱 오픈 채팅 참여 준비",
               description = """
                             `Authorization: Kakao {AccessToken}` 요구됨. 상단 [Authorize] 버튼으로 설정 요망
                             """,
               security = {@SecurityRequirement(name = "kakao-token")})
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
    @Parameters({
            @Parameter(name = "otAction", description = "`joinChat`으로 고정해야함.", required = true, example = "joinChat")
    })
    @PostMapping(value = "/prepare-join", params = "otAction=joinChat")
    public String prepareJoin(@AuthenticationPrincipal AuthenticatedUser user,
                              @Parameter(hidden = true) @SuppressWarnings("unused")
                              @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                              HttpServletRequest request, HttpServletResponse response) {
        String frontUrl = properties.getToolFrontUrl();
//        saveAuthentication(request, response);
        return "redirect:%s/openchat/join".formatted(frontUrl);
    }

    private void saveAuthentication(HttpServletRequest request, HttpServletResponse response) {
        HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
        SecurityContext context = SecurityContextHolder.getContextHolderStrategy().getContext();
        securityContextRepository.saveContext(context, request, response);
    }

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
    @ResponseBody
    public Object joinChatting(@Schema(description = "NFT SCA", example = "0xa9a95c5fef43830d5d67156a2582a2e793acb465")
                               @RequestParam("sca") Address sca,
                               @Schema(description = "Token ID", example = "35100240002")
                               @RequestParam("tokenId") TokenId tokenId,
                               @AuthenticationPrincipal AuthenticatedUser user,
                               HttpSession session) {
        log.info("Join user: {}", user);

        clearAuthentication(session);
        return null;
    }

    private void clearAuthentication(HttpSession session) {
        SecurityContextHolder.getContextHolderStrategy().clearContext();
        session.invalidate();
    }
}
