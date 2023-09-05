package com.klipwallet.membership.entity;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@SuppressWarnings("ClassCanBeRecord")
@Value
public class OperatorInvitation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1617023615761524540L;

    MemberId partnerId;
    String phoneNumber;

    public OperatorInvitation(@JsonProperty("partnerId") MemberId partnerId,
                              @JsonProperty("phoneNumber") String phoneNumber) {
        this.partnerId = partnerId;
        this.phoneNumber = phoneNumber;
    }
}
