package com.klipwallet.membership.controller.tool;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.chatroom.OperatorSummary;
import com.klipwallet.membership.dto.operator.OperatorCreate;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.OperatorService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.Operator", description = "Tool 운영진 API")
@RestController
@RequestMapping("/tool/v1/operator")
@RequiredArgsConstructor
public class OperatorController {
    private final OperatorService operatorService;

    @Operation(summary = "운영자 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "운영자 생성 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public OperatorSummary create(
            @Valid @RequestBody OperatorCreate command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return operatorService.create(command, user);
    }
}
