package com.klipwallet.membership.adaptor.kakao;

import feign.form.FormProperty;
import lombok.NonNull;

public class JoinOpenlinkReqDto {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType;
    @FormProperty("nickname")
    String nickname;
    @FormProperty("profile_image")
    String profileImage;
    @FormProperty("domain_id")
    Long domainId;
    @FormProperty("link_id")
    Long linkId;
    @FormProperty("ignore_kick_status")
    Boolean ignoreKickStatus;

    JoinOpenlinkReqDto(@NonNull String targetId, @NonNull String targetIdType, @NonNull String nickname, String profileImage,
                       @NonNull Long domainId, @NonNull Long linkId) {
        this.targetId = targetId;
        this.targetIdType = targetIdType;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.domainId = domainId;
        this.linkId = linkId;
        this.ignoreKickStatus = false; // 기획상 false로 고정
    }
}
