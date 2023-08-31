package com.klipwallet.membership.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.klipwallet.membership.dto.attachfile.AttachFileDto.MetaData;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.UploadType;
import com.klipwallet.membership.service.AttachFileService;

@Tag(name = "Admin.AttachFile", description = "Admin 첨부파일 API")
@RestController
@RequestMapping("/admin/v1/files")
@RequiredArgsConstructor
@Validated
public class AttachFileAdminController {
    private final AttachFileService attachFileService;

    @Operation(summary = "Admin 첨부파일 이미지 업로드",
               description = """
                             - Limit-Size: 2MB
                             - jpeg, png 이미지 파일만 업로드 가능""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid Upload File(Only jpg,png) or Limit-Size Over",
                         content = @Content(schema = @Schema(ref = "Error400File"))),
    })
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MetaData uploadImage(
            @Parameter(description = "업로드 할 이미지 파일", required = true) @ImageFile @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return attachFileService.create(new MultipartAttacheFile(file), UploadType.EDITOR, user);
    }
}
