package com.klipwallet.membership.adaptor.kakao.dto;

import feign.form.FormProperty;
import lombok.NonNull;

import static com.klipwallet.membership.adaptor.kakao.KakaoAdaptor.DEFAULT_TARGET_ID_TYPE;


public class LeaveOpenlinkReq {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType = DEFAULT_TARGET_ID_TYPE;
    @FormProperty("domain_id")
    Long domainId;
    @FormProperty("link_id")
    Long linkId;

    public LeaveOpenlinkReq(@NonNull String targetId,
                            @NonNull Long domainId, @NonNull Long linkId) {
        this.targetId = targetId;
        this.domainId = domainId;
        this.linkId = linkId;
    }
}
