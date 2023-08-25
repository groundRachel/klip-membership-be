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
    StorageResult store(Attachable command, MemberId memberId);
}
