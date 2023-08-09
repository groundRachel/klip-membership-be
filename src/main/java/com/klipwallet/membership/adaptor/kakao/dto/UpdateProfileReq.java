package com.klipwallet.membership.adaptor.kakao.dto;

import feign.form.FormProperty;
import lombok.NonNull;

import static com.klipwallet.membership.adaptor.kakao.KakaoAdaptor.DEFAULT_TARGET_ID_TYPE;

public class UpdateProfileReq {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType = DEFAULT_TARGET_ID_TYPE;
    @FormProperty("nickname")
    String nickname;
    @FormProperty("profile_image")
    String profileImage;
    @FormProperty("domain_id")
    Long domainId;
    @FormProperty("link_id")
    Long linkId;

    public UpdateProfileReq(@NonNull String targetId, @NonNull String nickname,
                            String profileImage, @NonNull Long domainId, @NonNull Long linkId) {
        this.targetId = targetId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.domainId = domainId;
        this.linkId = linkId;
    }
}
