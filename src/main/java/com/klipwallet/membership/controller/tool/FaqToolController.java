package com.klipwallet.membership.controller.tool;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.faq.FaqDetail;
import com.klipwallet.membership.dto.faq.FaqRow;
import com.klipwallet.membership.entity.Faq.Status;
import com.klipwallet.membership.service.FaqService;

@Tag(name = "Tool.FAQ", description = "Tool FAQ API")
@RestController
@RequestMapping("/tool/v1/faqs")
@RequiredArgsConstructor
public class FaqToolController {
    private final FaqService faqService;

    @Operation(summary = "FAQ 상세 조회")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "FAQ 조회 성공"),
                   @ApiResponse(responseCode = "404", description = "존재하지 않는 FAQ", content = @Content(schema = @Schema(ref = "Error404")))})
    @GetMapping("/{faqId}")
    public FaqDetail detail(@Parameter(description = "faq id", required = true, example = "2") @PathVariable Integer faqId) {
        return faqService.getLivedDetail(faqId);
    }

    @Operation(summary = "FAQ 목록 조회")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "FAQ 조회 성공"),})
    @GetMapping
    public Page<FaqRow> list(@ParameterObject @PageableDefault(size = 100, page = 1) Pageable page) {
        return faqService.listByStatus(Status.LIVE, page);
    }
}
