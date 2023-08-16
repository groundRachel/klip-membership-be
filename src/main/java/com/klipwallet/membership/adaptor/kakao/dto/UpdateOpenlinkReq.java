package com.klipwallet.membership.adaptor.kakao.dto;

import feign.form.FormProperty;
import lombok.NonNull;

import static com.klipwallet.membership.adaptor.kakao.KakaoAdaptor.DEFAULT_TARGET_ID_TYPE;


public class UpdateOpenlinkReq {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType = DEFAULT_TARGET_ID_TYPE;
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

    public UpdateOpenlinkReq(@NonNull String targetId, @NonNull Long domainId,
                             @NonNull Long linkId, String linkName, String linkImage, String linkDescription) {
        this.targetId = targetId;
        this.domainId = domainId;
        this.linkId = linkId;
        this.linkName = linkName;
        this.linkImage = linkImage;
        this.linkDescription = linkDescription;
    }
}
