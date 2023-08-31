package com.klipwallet.membership.controller;

import java.nio.charset.StandardCharsets;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;

import com.klipwallet.membership.entity.AttachFile;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.service.AttachFileService;
import com.klipwallet.membership.service.LocalTempStorageService;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Hidden
@Profile("local")
@Controller
@RequestMapping("/common/v1/temp-files")
@RequiredArgsConstructor
public class LocalTempFileController {
    private final AttachFileService attachFileService;
    private final LocalTempStorageService storageService;

    @GetMapping("/{objectId}")
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
