package com.klipwallet.membership.controller;

import java.nio.charset.StandardCharsets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriUtils;

import com.klipwallet.membership.adaptor.local.LocalTempStorageService;
import com.klipwallet.membership.entity.AttachFile;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.service.AttachFileService;
import com.klipwallet.membership.service.OperatorInvitable;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Tag(name = "Local.Common", description = "Local Helper API")
@Profile("local")
@Controller
@RequiredArgsConstructor
public class LocalController {
    private final AttachFileService attachFileService;
    private final LocalTempStorageService storageService;
    private final OperatorInvitable operatorInvitable;

    @Operation(summary = "Local 파일 다운로드", description = "local 환경에서 편의성 용도로만 사용할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
    })
    @GetMapping("/local/v1/temp-files/{objectId}")
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String objectId) {
        AttachFile file = attachFileService.tryGetAttachFile(new ObjectId(objectId));
        String fileName = UriUtils.encode(file.getFilename(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"%s\"".formatted(fileName);
        Resource resource = storageService.getResource(file.getObjectId());
        return ResponseEntity.ok()
                             .contentType(file.getContentType())
                             .contentLength(file.getContentLength().toBytes())
                             .header(CONTENT_DISPOSITION, contentDisposition)
                             .body(resource);

    }

    @Operation(summary = "Tool 운영진 초대", description = "local 환경에서 편의성 용도로만 사용할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "운영진 초대 URL 반환"),
    })
    @PostMapping("/tool/v1/operators/invite-local")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public InviteResult inviteOperator(@RequestParam String phone, @AuthenticationPrincipal AuthenticatedUser partner) {
        String invitationUrl = operatorInvitable.inviteOperator(partner.getMemberId(), phone);
        return new InviteResult(invitationUrl);
    }

    public record InviteResult(String invitationUrl) {
    }
}
