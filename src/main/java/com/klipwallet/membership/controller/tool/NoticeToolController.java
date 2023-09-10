package com.klipwallet.membership.controller.tool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.notice.NoticeDto;
import com.klipwallet.membership.dto.notice.NoticeDto.Row;
import com.klipwallet.membership.exception.NoticeNotFoundException;
import com.klipwallet.membership.service.NoticeService;

@Tag(name = "Tool.Notice", description = "Tool 공지사항 API")
@RestController
@RequestMapping("/tool/v1/notices")
@RequiredArgsConstructor
@Slf4j
public class NoticeToolController {
    private final NoticeService noticeService;

    @Operation(summary = "Tool 공지사항 목록 조회",
               description = """
                             - 해당 목록에서 **고정 공지는 노출되지 않는다.**
                             - sort 인자는 무시되며, 무조건 최근 Live일시 최신 순으로 정렬한다.(order by livedAt desc)
                             """)
    @ApiResponses(@ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"))
    @GetMapping
    public Page<Row> list(@ParameterObject Pageable pageable) {
        return noticeService.getLivedList(pageable);
    }

    @Operation(summary = "Tool 공지사항 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않는 공지사항", content = @Content(schema = @Schema(ref = "Error404")))
    })
    @GetMapping("/{noticeId}")
    public NoticeDto.Detail detail(
            @Parameter(description = "공지사항 id", required = true, example = "2") @PathVariable Integer noticeId) {
        return noticeService.getLivedDetail(noticeId);
    }

    @Operation(summary = "Tool 고정 공지 조회", description = "고정 공지가 존재 하지 않으면 최신 공지가 노출된다. 공지가 하나 없으면 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "고정 공지 조회 성공"),
            @ApiResponse(responseCode = "204", description = "존재 하지 않는 고정 공지")
    })
    @GetMapping("/primary")
    public ResponseEntity<NoticeDto.Row> primary() {
        try {
            return ResponseEntity.ok(noticeService.getPrimaryNoticeOrLatest());
        } catch (NoticeNotFoundException cause) {
            log.info("Not found primary or latest notice. return 204");
            return ResponseEntity.noContent().build();
        }
    }
}
