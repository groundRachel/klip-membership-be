package com.klipwallet.membership.adaptor.local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.klipwallet.membership.dto.storage.StorageResult;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.exception.NotFoundException;
import com.klipwallet.membership.exception.storage.StorageStoreException;
import com.klipwallet.membership.service.StorageService;

/**
 * Local {@link com.klipwallet.membership.service.StorageService}
 */
@SuppressWarnings("LombokGetterMayBeUsed")
@Profile("local")
@Component
@Slf4j
public class LocalTempStorageService implements StorageService {
    @Getter
    private final Path uploadPath;
    private final int serverPort;

    public LocalTempStorageService(@Value("${server.port:8080}") int serverPort) throws IOException {
        this.serverPort = serverPort;
        this.uploadPath = Files.createTempDirectory("attach-file");
        log.info("LocalTempStorageService.uploadPath: {}", uploadPath);
    }

    @Override
    public StorageResult store(Attachable command, String path, MemberId memberId) {
        try {
            Path tempFile = Files.createTempFile(uploadPath, "", "");
            FileCopyUtils.copy(command.getInputStream(), Files.newOutputStream(tempFile));
            log.info("Success store: {}", tempFile);
            ObjectId objectId = new ObjectId(tempFile.getFileName().toString());
            return new StorageResult(objectId, "http://localhost:%s/local/v1/temp-files/%s".formatted(serverPort, objectId.getValue()));
        } catch (IOException e) {
            throw new StorageStoreException(e);
        }
    }

    public Resource getResource(ObjectId objectId) {
        Path path = Paths.get(uploadPath.toString(), objectId.getValue());
        if (!path.toFile().exists() || !path.toFile().isFile()) {
            log.warn("Not exists file: {}", path);
            throw new NotFoundException();
        }
        return new PathResource(path);
    }
}
