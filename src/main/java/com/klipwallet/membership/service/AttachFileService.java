package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.attachfile.AttachFileDto.MetaData;
import com.klipwallet.membership.dto.storage.StorageResult;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.repository.AttachFile;
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
     * @return 생성된 첨부 파일 정보
     */
    @Transactional
    public MetaData create(Attachable command, AuthenticatedUser creator) {
        // 파일 저장
        StorageResult objectId = storageService.store(command, creator.getMemberId());
        // entity 생성
        AttachFile entity = toAttachFile(command, creator, objectId);
        AttachFile persistEntity = attachFileRepository.save(entity);
        return new MetaData(persistEntity);
    }

    @SuppressWarnings("DataFlowIssue")
    private AttachFile toAttachFile(Attachable command, AuthenticatedUser creator, StorageResult storageResult) {
        return new AttachFile(command, storageResult, creator.getMemberId());
    }
}
