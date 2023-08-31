package com.klipwallet.membership.service;

import com.klipwallet.membership.dto.storage.StorageResult;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.MemberId;

/**
 * 파일을 관리할 수 있는 인터페이스
 * <p>
 * 실제 구현은 AWS S3 에 의존
 * </p>
 */
public interface StorageService {
    /**
     * 파일 저장
     *
     * @param command  파일 정보와 byte를 제공하는 인터페이스
     * @param path     추가 경로명
     * @param memberId 업로더 ID
     * @return 저장된 파일 메타 정보
     */
    StorageResult store(Attachable command, String path, MemberId memberId);
}
