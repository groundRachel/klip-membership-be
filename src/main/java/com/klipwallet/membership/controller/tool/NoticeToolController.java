package com.klipwallet.membership.controller.tool;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.notice.NoticeDto.Row;
import com.klipwallet.membership.entity.Notice.Status;
import com.klipwallet.membership.service.NoticeService;

@Tag(name = "Tool.Notice", description = "Tool 공지사항 API")
@RestController
@RequestMapping("/tool/v1/notices")
@RequiredArgsConstructor
public class NoticeToolController {
    private final NoticeService noticeService;

    @Operation(summary = "Tool 공지사항 목록 조회")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"))
    @GetMapping
    public List<Row> list() {
        return noticeService.getListByStatus(Status.LIVE);
    }
}
