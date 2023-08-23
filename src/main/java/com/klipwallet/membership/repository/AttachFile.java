package com.klipwallet.membership.repository;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.MediaType;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.BaseEntity;
import com.klipwallet.membership.entity.LinkStatus;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.ObjectId;

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

    private MediaType contentType;

    private Long contentLength;

    @Embedded
    private ObjectId objectId;

    private String linkUrl;

    private LinkStatus linkStatus;

    @ForJpa
    protected AttachFile() {
    }

    public AttachFile(Attachable command, ObjectId objectId, String linkUrl, MemberId creatorId) {
        this.filename = command.getFilename();
        this.contentType = command.getContentType();
        this.contentLength = command.getBytesSize();
        this.objectId = objectId;
        this.linkUrl = linkUrl;
        this.linkStatus = LinkStatus.UNLINK;
        createBy(creatorId);
    }
}
