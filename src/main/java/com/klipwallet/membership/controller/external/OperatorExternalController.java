package com.klipwallet.membership.controller.external;

import jakarta.servlet.http.HttpSession;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
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
            @ApiResponse(responseCode = "400",
                         description = """
                                       - code: `400009`: 초대한 운영진은 Klip 이용자가 아닙니다. {phoneNumber}
                                       - code: `400007`: 운영진 초대자와 현재 접속자가 일치하지 않습니다.
                                       """,
                         content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403", description = "카카오 인증이 되지 않은 경우", content = @Content(schema = @Schema(ref = "Error403"))),
            @ApiResponse(responseCode = "404", description = "code: `404003`: 초대한 파트너가 존재하지 않는 경우",
                         content = @Content(schema = @Schema(ref = "Error404"))),
            @ApiResponse(responseCode = "409", description = "code: `409003`: 이미 가입된 운영진입니다. {0}",
                         content = @Content(schema = @Schema(ref = "Error409"))),
            @ApiResponse(responseCode = "410", description = "code: `410002`: 운영진 초대 기한이 만료됐습니다.(24시간)",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public OperatorSummary create(
            @Parameter(hidden = true) @SessionAttribute(value = OperatorInvitation.STORE_KEY, required = false) String invitationCode,
            @AuthenticationPrincipal AuthenticatedUser user, HttpSession session) {

        OperatorSummary result = operatorService.join(invitationCode, user);
        session.removeAttribute(OperatorInvitation.STORE_KEY);  // 세선 내 초대 코드 제거
        return result;
    }
}
