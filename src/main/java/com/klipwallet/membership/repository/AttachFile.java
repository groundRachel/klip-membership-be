package com.klipwallet.membership.repository;

import java.util.function.Supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.MediaType;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.BaseEntity;
import com.klipwallet.membership.entity.LinkStatus;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.exception.InvalidRequestException;

/**
 * 첨부파일 Entity
 */
@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(callSuper = true)
public class AttachFile extends BaseEntity<AttachFile> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String filename;
    @Column(nullable = false)
    private MediaType contentType;
    @Column(nullable = false)
    private Long contentLength;
    @Embedded
    private ObjectId objectId;
    @Column(nullable = false)
    private String linkUrl;
    @Column(nullable = false)
    private LinkStatus linkStatus;

    @ForJpa
    protected AttachFile() {
    }

    public AttachFile(Attachable command, @NonNull ObjectId objectId, @NonNull String linkUrl, @NonNull MemberId creatorId) {
        this.filename = command.getFilename();
        this.contentType = verifiedNonNull(command.getContentType(), () -> "'contentType' is empty");
        verified(command.getBytesSize() > 0, () -> "'contentLength' is 0");
        this.contentLength = command.getBytesSize();
        this.objectId = objectId;
        this.linkUrl = linkUrl;
        this.linkStatus = LinkStatus.UNLINK;
        createBy(creatorId);
    }

    private void verified(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new InvalidRequestException(messageSupplier.get());
        }
    }

    private <T> T verifiedNonNull(T target, Supplier<String> messageSupplier) {
        if (target == null) {
            throw new InvalidRequestException(messageSupplier.get());
        }
        return target;
    }
}
