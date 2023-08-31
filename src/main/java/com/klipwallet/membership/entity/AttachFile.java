package com.klipwallet.membership.entity;

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
import org.springframework.util.unit.DataSize;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.dto.storage.StorageResult;
import com.klipwallet.membership.exception.InvalidRequestException;

/**
 * 첨부파일 Entity
 */
@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(callSuper = true)
public class AttachFile extends BaseEntity<AttachFile> {
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 원본 파일명(거의 사용할 일이 없으나, 파일 다운로드가 추후 구현된다면 사용될 수 있음.
     */
    private String filename;
    @Column(nullable = false)
    private MediaType contentType;
    /**
     * 파일 byte 사이즈
     */
    @Column(nullable = false)
    private DataSize contentLength;
    /**
     * 스토리지 서비스(ex: S3)의 ObjectId
     */
    @Embedded
    private ObjectId objectId;
    /**
     * 업로드 된 파일에 접근 가능한 실제 URL
     * <pre>
     * ex: {@code https://media.klipwallet.com/klip-membership/3/228492df-e771-43cb-b22e-1aba918c98ef}
     * </pre>
     */
    @Column(nullable = false)
    private String linkUrl;
    /**
     * 파일 연결 상태
     * <p>
     * UNLINK: 공지 사항 본문에 해당 파일이 링크되어 있지만 관계가 없어서 확인할 방도가 없음 = <b>고아 객체</b><br/>
     * LINKED: 공지 사항에서 해당 파일을 사용하고 있으면 Notice-AttachFile 관계 Entity를 통해서 AttachFile Entity를 참조하고 있음.
     * 만약 LINKED 상태라면 공지사항 삭제 시 Notice-AttachFile 관계 Entity를 통해서 첨부파일도 같이 삭제할 수 있음.
     * </p>
     */
    @Column(nullable = false)
    private LinkStatus linkStatus;

    @ForJpa
    protected AttachFile() {
    }

    public AttachFile(Attachable command, @NonNull StorageResult storageResult, @NonNull MemberId creatorId) {
        this.filename = command.getFileName();
        this.contentType = verifiedNonNull(command.getContentType(), () -> "'contentType' is empty");
        verified(command.getSize().toBytes() > 0, () -> "'contentLength' is 0");
        this.contentLength = command.getSize();
        this.objectId = storageResult.objectId();
        this.linkUrl = storageResult.objectUrl();
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
