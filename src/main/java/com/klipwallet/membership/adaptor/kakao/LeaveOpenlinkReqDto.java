package com.klipwallet.membership.adaptor.kakao;

import feign.form.FormProperty;
import lombok.NonNull;

public class LeaveOpenlinkReqDto {
    @FormProperty("target_id")
    String targetId;
    @FormProperty("target_id_type")
    String targetIdType;
    @FormProperty("domain_id")
    Long domainId;
    @FormProperty("link_id")
    Long linkId;

    LeaveOpenlinkReqDto(@NonNull String targetId, @NonNull String targetIdType,
                        @NonNull Long domainId, @NonNull Long linkId) {
        this.targetId = targetId;
        this.targetIdType = targetIdType;
        this.domainId = domainId;
        this.linkId = linkId;
    }
}
