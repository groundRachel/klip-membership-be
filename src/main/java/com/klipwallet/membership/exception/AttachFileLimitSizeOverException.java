package com.klipwallet.membership.exception;

import org.springframework.util.unit.DataSize;

import com.klipwallet.membership.entity.UploadType;

/**
 * 파일 업로드 제한 사이즈를 초과했습니다. limit: {0,number} KB, upload: {1,number} KB
 */
@SuppressWarnings("serial")
public class AttachFileLimitSizeOverException extends InvalidRequestException {

    public AttachFileLimitSizeOverException(UploadType type, DataSize uploadBytes) {
        super(ErrorCode.ATTACH_FILE_UPLOAD_LIMIT_OVER, type.getLimit().toKilobytes(), uploadBytes.toKilobytes());
    }
}
