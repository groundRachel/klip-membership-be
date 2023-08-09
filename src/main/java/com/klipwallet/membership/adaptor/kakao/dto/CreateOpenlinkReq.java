package com.klipwallet.membership.adaptor.kakao.dto;

import feign.form.FormProperty;
import lombok.NonNull;

import static com.klipwallet.membership.adaptor.kakao.KakaoAdaptor.DEFAULT_TARGET_ID_TYPE;

public class CreateOpenlinkReq {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType = DEFAULT_TARGET_ID_TYPE;
    @FormProperty("domain_id")
    Long domainId;
    @FormProperty("link_name")
    String linkName;
    @FormProperty("link_image")
    String linkImage;
    @FormProperty("link_description")
    String linkDescription;
    @FormProperty("nickname")
    String nickname;
    @FormProperty("profile_image")
    String profileImage;

    public CreateOpenlinkReq(@NonNull String targetId, @NonNull Long domainId, @NonNull String linkName, String linkImage, String linkDescription,
                             @NonNull String nickname, String profileImage) {
        this.targetId = targetId;
        this.domainId = domainId;
        this.linkName = linkName;
        this.linkImage = linkImage;
        this.linkDescription = linkDescription;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
