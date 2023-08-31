package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.AttachFile;
import com.klipwallet.membership.entity.ObjectId;

public interface AttachFileRepository extends JpaRepository<AttachFile, String> {
    Optional<AttachFile> findByObjectId(ObjectId objectId);
}
