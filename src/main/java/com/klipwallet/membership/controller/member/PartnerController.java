package com.klipwallet.membership.controller.member;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.ApplyResult;
import com.klipwallet.membership.service.PartnerService;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/tool/partners")
@RequiredArgsConstructor
public class PartnerController {
    private final PartnerService partnerService;

    @Operation(summary = "파트너 가입 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "구글 연동 안됨",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @Secured("ROLE_PARTNER")
    @PostMapping("/apply")
    @ResponseStatus(CREATED)
    public ApplyResult apply(@Valid @RequestBody PartnerDto.Apply body) {
        return partnerService.apply(body);
    }
}
