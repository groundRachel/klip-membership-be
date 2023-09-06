package com.klipwallet.membership.entity;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

import com.klipwallet.membership.entity.utils.PhoneNumberUtils;

@SuppressWarnings("ClassCanBeRecord")
@Value
public class OperatorInvitation implements Serializable {
    /**
     * 초대 코드를 일시적으로 보관할 때 사용하는 키
     * <p>
     * session, redis, file-system 등에 보관할 때 사용.
     * </p>
     */
    public static final String STORE_KEY = "km.invitation-code";
    @Serial
    private static final long serialVersionUID = 1617023615761524540L;
    /**
     * 운영진을 초대한 파트너 아이디
     */
    MemberId partnerId;
    /**
     * 초대 받은 운영진의 휴대폰 번호
     * <p>
     * 카카오 알림톡 발송을 위한 휴대폰 번호이다.<br/>
     * 숫자만으로 저장되어 있다. ex: 01025801357
     * </p>
     *
     * @see com.klipwallet.membership.entity.utils.PhoneNumberUtils#isFormalKrMobileNumber(String)
     */
    String mobileNumber;

    public OperatorInvitation(@NonNull @JsonProperty("partnerId") MemberId partnerId,
                              @NonNull @JsonProperty("mobileNumber") String mobileNumber) {
        this.partnerId = partnerId;
        this.mobileNumber = verifiedMobileNumber(mobileNumber);
    }

    private String verifiedMobileNumber(String mobileNumber) {
        if (!PhoneNumberUtils.isFormalKrMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException("Invalid mobileNumber. %s".formatted(mobileNumber));
        }
        return mobileNumber;
    }
}
