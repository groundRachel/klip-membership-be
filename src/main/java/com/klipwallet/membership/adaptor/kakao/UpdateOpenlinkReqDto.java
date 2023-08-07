package com.klipwallet.membership.adaptor.kakao;

import feign.form.FormProperty;
import lombok.NonNull;

public class UpdateOpenlinkReqDto {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType;
    @FormProperty("domain_id")
    Long domainId;
    @FormProperty("link_id")
    Long linkId;
    @FormProperty("link_name")
    String linkName;
    @FormProperty("link_image")
    String linkImage;
    @FormProperty("link_description")
    String linkDescription;

    UpdateOpenlinkReqDto(@NonNull String targetId, @NonNull String targetIdType, @NonNull Long domainId,
                         @NonNull Long linkId, String linkName, String linkImage, String linkDescription) {
        this.targetId = targetId;
        this.targetIdType = targetIdType;
        this.domainId = domainId;
        this.linkId = linkId;
        this.linkName = linkName;
        this.linkImage = linkImage;
        this.linkDescription = linkDescription;
    }
}
