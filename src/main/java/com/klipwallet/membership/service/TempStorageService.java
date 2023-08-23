package com.klipwallet.membership.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.exception.storage.StorageStoreException;

/**
 * Mock {@link com.klipwallet.membership.service.StorageService}
 *
 * @deprecated 추후 S3 구현체가 생기면 제거 요망
 */
@Deprecated
@Service
@Slf4j
public class TempStorageService implements StorageService {
    private final Path uploadPath;

    public TempStorageService() throws IOException {
        this.uploadPath = Files.createTempDirectory("attach-file");
        log.info("tempUploadPath: {}", uploadPath);
    }

    @Override
    public ObjectId store(Attachable command) {
        ObjectId objectId = new ObjectId(UUID.randomUUID().toString());
        try {
            Path tempFile = Files.createTempFile(uploadPath, objectId.getValue(), null);
            log.info("Success store: {}", tempFile);
        } catch (IOException e) {
            throw new StorageStoreException(e);
        }
        return objectId;
    }
}
