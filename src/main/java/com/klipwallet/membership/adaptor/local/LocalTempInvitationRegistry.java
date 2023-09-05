package com.klipwallet.membership.adaptor.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.service.InvitationRegistry;

/**
 * Local 환경 전용 Temp FileSystem에 의존하는 {@link com.klipwallet.membership.service.InvitationRegistry}
 * <p>
 * 24시간 제한은 없고, Temp Directory 와 라이프사이클이 같음.
 * </p>
 */
@Profile("local")
@Component
@Slf4j
public class LocalTempInvitationRegistry implements InvitationRegistry {
    private final Path registryPath;
    private final ObjectMapper objectMapper;

    public LocalTempInvitationRegistry(ObjectMapper objectMapper) throws IOException {
        this.registryPath = Files.createTempDirectory("invitation-registry");
        log.info("LocalTempInvitationRegistry.registryPath: {}", registryPath);
        this.objectMapper = objectMapper;
    }

    @NonNull
    @Override
    public String save(OperatorInvitation invitation) {
        try {
            Path tempFile = Files.createTempFile(registryPath, "", "");
            objectMapper.writeValue(tempFile.toFile(), invitation);
            log.info("Success store: {}", tempFile);
            return tempFile.getFileName().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public OperatorInvitation lookup(String invitationCode) {
        Path path = Paths.get(registryPath.toString(), invitationCode);
        File file = path.toFile();
        if (!file.exists() || !path.toFile().isFile()) {
            return null;
        }
        try {
            return objectMapper.readValue(file, OperatorInvitation.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
