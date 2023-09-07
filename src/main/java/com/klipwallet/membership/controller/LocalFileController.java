package com.klipwallet.membership.controller;

import java.nio.charset.StandardCharsets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;

import com.klipwallet.membership.adaptor.local.LocalTempStorageService;
import com.klipwallet.membership.entity.AttachFile;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.service.AttachFileService;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Tag(name = "Local.Common", description = "Local File API")
@Profile("local")
@Controller
@RequiredArgsConstructor
public class LocalFileController {
    private final AttachFileService attachFileService;
    private final LocalTempStorageService storageService;

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
}
