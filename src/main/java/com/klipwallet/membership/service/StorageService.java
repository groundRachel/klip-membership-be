package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.S3ObjectResult;

/**
 * 파일을 관리할 수 있는 인터페이스
 * <p>
 * 실제 구현은 AWS S3 에 의존
 * </p>
 */
public interface StorageService {
    S3ObjectResult store(Attachable command, MemberId memberId);
}
