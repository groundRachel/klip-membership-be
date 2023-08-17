package com.klipwallet.membership.controller.tool;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.service.PartnerService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.Partners", description = "파트너 API")
@RestController
@RequestMapping("/tool/v1/partners")
@RequiredArgsConstructor
public class PartnersToolController {
    private final PartnerService partnerService;

    @Operation(summary = "파트너 가입 요청", description = "파트너가 구글 인증 후 가입 요청 보냄. GX의 admin이 승인을 해야 가입됨.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "구글 연동 안됨",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    //    @Secured("OAUTH2_USER") // TODO KLDV-3077 check role
    @PostMapping("/apply")
    @ResponseStatus(CREATED)
    public PartnerDto.ApplyResult apply(@Valid @RequestBody PartnerDto.Apply body) {
        return partnerService.apply(body);
    }
}
