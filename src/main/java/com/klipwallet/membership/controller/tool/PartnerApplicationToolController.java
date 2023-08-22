package com.klipwallet.membership.controller.tool;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto;
import com.klipwallet.membership.service.PartnerApplicationService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.PartnerApplication", description = "파트너 계정 관리 API")
@RestController
@RequestMapping("/tool/v1/partner-applications")
@RequiredArgsConstructor
public class PartnerApplicationToolController {
    private final PartnerApplicationService partnerApplicationService;

    @Operation(summary = "파트너 가입 요청", description = "파트너가 구글 인증 후 가입 요청 보냄. GX의 admin이 승인을 해야 가입됨.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "403", description = "파트너 가입 요청 권한 없음", content = @Content(schema = @Schema(ref = "Error403")))
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public PartnerApplicationDto.ApplyResult apply(@Valid @RequestBody PartnerApplicationDto.Application body) {
        return partnerApplicationService.apply(body);
    }
}
