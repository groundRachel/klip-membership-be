package com.klipwallet.membership.service;

import java.time.Duration;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.klipwallet.membership.entity.OperatorInvitation;

/**
 * 초대 Registry 컴포넌트
 * <p>
 * 영속화 되지는 않고 초대한 내용에 대해서 24시간 한시적으로 관리하는 컴포넌트
 * </p>
 */
public interface InvitationRegistry {
    Duration DEFAULT_TIMEOUT = Duration.ofHours(24);

    /**
     * 초대 Registry에 저장 (24시간 이후 휘발됨)
     *
     * @param invitation 초대 정보
     * @return 초대 코드(UUID)
     */
    @Nonnull
    String save(OperatorInvitation invitation);

    /**
     * 초대 Registry에서 코드로 조회
     *
     * @param invitationCode 조회할 초대 코드(UUID)
     * @return 저장된 초대 정보. 만약 존재 하지 않으면 {@code null} 반환
     */
    @Nullable
    OperatorInvitation lookup(String invitationCode);

    /**
     * 초대 코드 삭제
     * <p>해당 코드는 1회용이기 때문에 해당 코드로 초대가 완료되면, 삭제한다.</p>
     *
     * @param invitationCode 삭제할 초대 코드
     */
    void delete(String invitationCode);
}
