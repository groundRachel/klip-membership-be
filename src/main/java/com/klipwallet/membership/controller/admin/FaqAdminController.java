package com.klipwallet.membership.controller.admin;


import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.faq.FaqCreate;
import com.klipwallet.membership.dto.faq.FaqDetail;
import com.klipwallet.membership.dto.faq.FaqRow;
import com.klipwallet.membership.dto.faq.FaqStatus;
import com.klipwallet.membership.dto.faq.FaqSummary;
import com.klipwallet.membership.dto.faq.FaqUpdate;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Faq;
import com.klipwallet.membership.service.FaqService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Admin.FAQ", description = "Admin FAQ API")
@RestController
@RequestMapping("/admin/v1/faqs")
@RequiredArgsConstructor
public class FaqAdminController {
    private final FaqService faqService;

    @Operation(summary = "FAQ 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "FAQ 작성 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public FaqSummary create(
            @Valid @RequestBody FaqCreate command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return faqService.create(command, user);
    }

    @Operation(summary = "FAQ 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "FAQ 수정 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 FAQ", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PutMapping("/{faqId}")
    public FaqDetail update(
            @Parameter(description = "faq id", required = true, example = "2") @PathVariable Integer faqId,
            @Valid @RequestBody FaqUpdate command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return faqService.update(faqId, command, user);
    }

    @Operation(summary = "FAQ 샹태 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "FAQ 수정 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(ref = "Error400"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 FAQ", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @PutMapping("/{faqId}/status")
    public FaqStatus changeStatus(
            @Parameter(description = "faq id", required = true, example = "2") @PathVariable Integer faqId,
            @Valid @RequestBody FaqStatus command,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return faqService.changeStatus(faqId, command, user);
    }

    @Operation(summary = "FAQ 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "FAQ 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 FAQ", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @GetMapping("/{faqId}")
    public FaqDetail detail(
            @Parameter(description = "faq id", required = true, example = "2") @PathVariable Integer faqId) {
        return faqService.getDetail(faqId);
    }

    @Operation(summary = "FAQ 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "FAQ 조회 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid Query", content = @Content(schema = @Schema(ref = "Error400"))),
    })
    @GetMapping
    public Page<FaqRow> list(
            @Parameter(description = "필터링 할 FAQ 상태", example = "live") @RequestParam(value = "status", required = false) Faq.Status status,
            @ParameterObject @PageableDefault(size = 100, page = 1) Pageable page) {
        return faqService.listByStatus(status, page);
    }
}
