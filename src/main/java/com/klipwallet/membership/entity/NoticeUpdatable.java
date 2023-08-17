package com.klipwallet.membership.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public interface NoticeUpdatable {
    /**
     * 변경할 제목
     */
    @Nonnull
    String getTitle();

    /**
     * 변경한 본문
     */
    @Nonnull
    String getBody();

    /**
     * 메인 공지 여부. 값이 {@code null} 이면 변경하지 않는다.
     */
    @Nullable
    Boolean isMain();

    /**
     * 수정 관리자 아이디
     */
    @Nonnull
    MemberId getUpdatedBy();
}
