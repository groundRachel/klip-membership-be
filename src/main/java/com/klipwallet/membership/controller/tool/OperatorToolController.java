package com.klipwallet.membership.controller.tool;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.PhoneNumber;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.OperatorService;

@Tag(name = "Tool.Operator", description = "Tool 운영진 API")
@RestController
@RequestMapping("/tool/v1/operators")
@RequiredArgsConstructor
public class OperatorToolController {
    private final OperatorService operatorService;

    @Operation(summary = "Tool 운영진 초대")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "운영진 초대 알림톡 전송 성공"),
            @ApiResponse(responseCode = "400",
                         description = """
                                       - code: `400000`: Invalid Query
                                       - code: `400009`: 초대한 운영진은 Klip 이용자가 아닙니다.
                                       - code: `400011`: 이미 등록된 운영진은 초대할 수 없습니다.
                                       """, content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "500",
                         description = "알림톡 발송 오류 등 내부 서버 오류")
    })
    @PostMapping("/invite")
    @ResponseBody
    public void inviteOperator(@RequestParam @PhoneNumber String phone, @AuthenticationPrincipal AuthenticatedUser partner) {
        operatorService.inviteOperator(partner.getMemberId(), phone);
    }
}
