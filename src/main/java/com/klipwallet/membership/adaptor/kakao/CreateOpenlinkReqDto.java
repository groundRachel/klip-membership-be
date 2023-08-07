package com.klipwallet.membership.adaptor.kakao;

import feign.form.FormProperty;
import lombok.NonNull;


public class CreateOpenlinkReqDto {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType;
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

    CreateOpenlinkReqDto(@NonNull String targetId, @NonNull String targetIdType, @NonNull Long domainId, @NonNull String linkName,
                         String linkImage, String linkDescription, @NonNull String nickname, String profileImage) {
        this.targetId = targetId;
        this.targetIdType = targetIdType;
        this.domainId = domainId;
        this.linkName = linkName;
        this.linkImage = linkImage;
        this.linkDescription = linkDescription;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
