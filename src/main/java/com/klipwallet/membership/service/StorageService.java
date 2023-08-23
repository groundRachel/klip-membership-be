package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.ObjectId;

/**
 * 파일을 관리할 수 있는 인터페이스
 * <p>
 * 실제 구현은 AWS S3 에 의존
 * </p>
 */
public interface StorageService {

    /**
     * 파일을 저장
     *
     * @param command 파일 저장 인터페이스
     * @return 저장된 파일 정보
     */
    ObjectId store(Attachable command);
}
