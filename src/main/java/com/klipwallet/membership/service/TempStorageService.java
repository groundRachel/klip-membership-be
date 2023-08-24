package com.klipwallet.membership.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import com.klipwallet.membership.dto.storage.StorageResult;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.exception.storage.StorageStoreException;

/**
 * Mock {@link com.klipwallet.membership.service.StorageService}
 *
 * @deprecated 추후 S3 구현체가 생기면 제거 요망
 */
@Deprecated
@Slf4j
public class TempStorageService implements StorageService {
    private final Path uploadPath;

    public TempStorageService() throws IOException {
        this.uploadPath = Files.createTempDirectory("attach-file");
        log.info("tempUploadPath: {}", uploadPath);
    }

    @Override
    public StorageResult store(Attachable command, MemberId memberId) {
        ObjectId objectId = new ObjectId(UUID.randomUUID().toString());
        try {
            Path tempFile = Files.createTempFile(uploadPath, objectId.getValue(), null);
            log.info("Success store: {}", tempFile);
        } catch (IOException e) {
            throw new StorageStoreException(e);
        }
        return new StorageResult(objectId, "file://temp/%s".formatted(objectId.toString()));
    }
}
