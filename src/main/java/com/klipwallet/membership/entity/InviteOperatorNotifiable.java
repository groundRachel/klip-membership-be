package com.klipwallet.membership.entity;

import lombok.NonNull;

/**
 * 운영진 초대 알림을 위한 DTO
 *
 * @param kakaoUserId 초대 받은 운영진의 휴대폰 번호
 * @param invitationUrl       초대 URL
 * @param partnerName         파트너사회사
 */
public record InviteOperatorNotifiable(@NonNull String kakaoUserId,
                                       @NonNull String invitationUrl,
                                       @NonNull String partnerName
) {
}
