package com.klipwallet.membership.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.attachfile.AttachFileDto.MetaData;
import com.klipwallet.membership.dto.storage.StorageResult;
import com.klipwallet.membership.entity.AttachFile;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.entity.UploadType;
import com.klipwallet.membership.exception.AttachFileLimitSizeOverException;
import com.klipwallet.membership.exception.NotFoundException;
import com.klipwallet.membership.repository.AttachFileRepository;

@Service
@RequiredArgsConstructor
public class AttachFileService {
    private final AttachFileRepository attachFileRepository;
    private final StorageService storageService;

    /**
     * 첨부파일 생성(업로드)
     *
     * @param command 업로드 인자
     * @param type    업로드 타입
     * @return 생성된 첨부 파일 정보
     * @throws AttachFileLimitSizeOverException 업로드 제한 사이즈 초과
     */
    @Transactional
    public MetaData create(Attachable command, UploadType type, AuthenticatedUser creator) {
        // 업로드 초과 확인
        checkLimitSize(command, type);
        // 파일 저장
        StorageResult objectId = storageService.store(command, type.toDisplay(), creator.getMemberId());
        // entity 생성
        AttachFile entity = toAttachFile(command, creator, objectId);
        AttachFile persistEntity = attachFileRepository.save(entity);
        return new MetaData(persistEntity);
    }

    private void checkLimitSize(Attachable command, UploadType type) {
        if (command.getSize().compareTo(type.getLimit()) > 0) {
            throw new AttachFileLimitSizeOverException(type, command.getSize());
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private AttachFile toAttachFile(Attachable command, AuthenticatedUser creator, StorageResult storageResult) {
        return new AttachFile(command, storageResult, creator.getMemberId());
    }

    @Transactional(readOnly = true)
    public AttachFile tryGetAttachFile(@NonNull ObjectId objectId) {
        return attachFileRepository.findByObjectId(objectId)
                                   .orElseThrow(NotFoundException::new);
    }
}
