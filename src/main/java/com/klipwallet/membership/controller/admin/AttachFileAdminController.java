package com.klipwallet.membership.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.klipwallet.membership.adaptor.spring.validation.ImageFile;
import com.klipwallet.membership.controller.dto.MultipartAttacheFile;
import com.klipwallet.membership.dto.attachfile.AttachFileDto;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.AttachFileService;

@RestController
@RequestMapping("/admin/v1/files")
@RequiredArgsConstructor
@Validated
public class AttachFileAdminController {
    private final AttachFileService attachFileService;

    @PostMapping("/upload-image")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachFileDto.Meta upload(@ImageFile @RequestPart("file") MultipartFile file,
                                     @AuthenticationPrincipal AuthenticatedUser user) {
        return attachFileService.create(new MultipartAttacheFile(file), user);
    }
}
