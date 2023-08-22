package com.klipwallet.membership.entity;

import jakarta.annotation.Nonnull;

public interface FaqUpdatable {
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
     * 수정 관리자 아이디
     */
    @Nonnull
    MemberId getUpdater();
}
