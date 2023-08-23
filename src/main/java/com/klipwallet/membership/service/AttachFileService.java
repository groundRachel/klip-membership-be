package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.attachfile.AttachFileDto;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.repository.AttachFile;
import com.klipwallet.membership.repository.AttachFileRepository;

@Service
@RequiredArgsConstructor
public class AttachFileService {
    private final AttachFileRepository attachFileRepository;
    private final StorageService storageService;
    private final StorageHelper storageHelper;

    /**
     * 첨부파일 생성(업로드)
     *
     * @param command 업로드 인자
     * @return 생성된 첨부 파일 정보
     */
    @Transactional
    public AttachFileDto.Meta create(Attachable command, AuthenticatedUser creator) {
        // 파일 저장
        ObjectId objectId = storageService.store(command);
        // entity 생성
        AttachFile entity = toAttacheFile(command, creator, objectId);
        AttachFile persistEntity = attachFileRepository.save(entity);
        return new AttachFileDto.Meta(persistEntity);
    }

    @SuppressWarnings("DataFlowIssue")
    private AttachFile toAttacheFile(Attachable command, AuthenticatedUser creator, ObjectId objectId) {
        String linkUrl = storageHelper.toLinkUrl(command);
        return new AttachFile(command, objectId, linkUrl, creator.getMemberId());
    }
}
