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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.klipwallet.membership.dto.operator.OperatorSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.service.OperatorService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "External.Operator", description = "외부 운영진 API")
@RestController
@RequestMapping("/external/v1/operators")
@RequiredArgsConstructor
public class OperatorExternalController {
    private final OperatorService operatorService;

    @Operation(summary = "운영자 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "운영자 가입 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public OperatorSummary create(
            @SessionAttribute(value = OperatorInvitation.STORE_KEY, required = false) String invitationCode,
            @AuthenticationPrincipal AuthenticatedUser user,
            HttpSession session) {

        OperatorSummary result = operatorService.join(invitationCode, user);
        session.removeAttribute(OperatorInvitation.STORE_KEY);  // 세선 내 초대 코드 제거
        return result;
    }
}
